package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.exception.CarAlreadyParkedException;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.CarService;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingEntryServiceImplTest {

    @Mock
    private CarService carService;

    @Mock
    private ParkingSpotService spotService;

    @Mock
    private ParkingTransactionRepository transactionRepo;

    @InjectMocks
    private ParkingEntryServiceImpl entryService;

    private CarEntryRequest validRequest;
    private Car testCar;
    private ParkingSpot testSpot;
    private ParkingTransaction testTransaction;

    @BeforeEach
    void setUp() {
        validRequest = new CarEntryRequest("A123BC", CarType.PASSENGER);
        testCar = new Car();
        testCar.setLicensePlate("A123BC");
        testCar.setType(CarType.PASSENGER);

        testSpot = new ParkingSpot();
        testSpot.setId(1L);
        testSpot.setSpotType(CarType.PASSENGER);

        testTransaction = ParkingTransaction.builder()
                .car(testCar)
                .spot(testSpot)
                .build();
    }

    @Test
    void registerEntry_Success() {
        when(carService.findOrCreateCar(anyString(), any(CarType.class))).thenReturn(testCar);
        when(spotService.occupySpot(any(CarType.class))).thenReturn(testSpot);
        when(transactionRepo.existsByCarAndExitTimeIsNull(any(Car.class))).thenReturn(false);
        when(transactionRepo.save(any(ParkingTransaction.class))).thenReturn(testTransaction);

        ParkingTransaction result = entryService.registerEntry(validRequest);

        assertNotNull(result);
        assertEquals(testCar, result.getCar());
        assertEquals(testSpot, result.getSpot());
        verify(carService).findOrCreateCar("A123BC", CarType.PASSENGER);
        verify(spotService).occupySpot(CarType.PASSENGER);
        verify(transactionRepo).save(any(ParkingTransaction.class));
    }

    @Test
    void registerEntry_CarAlreadyParked() {
        when(carService.findOrCreateCar(anyString(), any(CarType.class))).thenReturn(testCar);
        when(transactionRepo.existsByCarAndExitTimeIsNull(any(Car.class))).thenReturn(true);

        assertThrows(CarAlreadyParkedException.class, () -> entryService.registerEntry(validRequest));
        verify(carService).findOrCreateCar("A123BC", CarType.PASSENGER);
        verify(spotService, never()).occupySpot(any(CarType.class));
        verify(transactionRepo, never()).save(any(ParkingTransaction.class));
    }

    @Test
    void registerEntry_NoAvailableSpots() {
        when(carService.findOrCreateCar(anyString(), any(CarType.class))).thenReturn(testCar);
        when(transactionRepo.existsByCarAndExitTimeIsNull(any(Car.class))).thenReturn(false);
        when(spotService.occupySpot(any(CarType.class))).thenThrow(new NoAvailableSpotsException("No spots available"));

        assertThrows(NoAvailableSpotsException.class, () -> entryService.registerEntry(validRequest));
        verify(carService).findOrCreateCar("A123BC", CarType.PASSENGER);
        verify(spotService).occupySpot(CarType.PASSENGER);
        verify(transactionRepo, never()).save(any(ParkingTransaction.class));
    }

    @Test
    void registerEntry_NormalizesLicensePlate() {
        CarEntryRequest requestWithSpaces = new CarEntryRequest("A 123-BC", CarType.PASSENGER);
        when(carService.findOrCreateCar("A123BC", CarType.PASSENGER)).thenReturn(testCar);
        when(spotService.occupySpot(any(CarType.class))).thenReturn(testSpot);
        when(transactionRepo.existsByCarAndExitTimeIsNull(any(Car.class))).thenReturn(false);
        when(transactionRepo.save(any(ParkingTransaction.class))).thenReturn(testTransaction);

        ParkingTransaction result = entryService.registerEntry(requestWithSpaces);

        assertNotNull(result);
        verify(carService).findOrCreateCar("A123BC", CarType.PASSENGER);
    }
} 