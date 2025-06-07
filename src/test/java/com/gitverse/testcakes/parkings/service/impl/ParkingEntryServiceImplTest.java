package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.exception.CarAlreadyParkedException;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.gitverse.testcakes.parkings.util.TestData.TEST_CAR_TYPE;
import static com.gitverse.testcakes.parkings.util.TestData.TEST_LICENSE_PLATE;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestCar;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestSpot;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestTransaction;
import static com.gitverse.testcakes.parkings.util.TestData.buildValidEntryRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingEntryServiceImplTest {

    @Mock
    private CarServiceImpl carService;

    @Mock
    private ParkingSpotServiceImpl spotService;

    @Mock
    private ParkingTransactionRepository transactionRepository;

    @InjectMocks
    private ParkingEntryServiceImpl entryService;

    private Car testCar;
    private ParkingSpot testSpot;
    private ParkingTransaction testTransaction;
    private CarEntryRequest testRequest;

    @BeforeEach
    void setUp() {
        testCar = buildTestCar();
        testSpot = buildTestSpot();
        testTransaction = buildTestTransaction();
        testRequest = buildValidEntryRequest();
    }

    @Test
    void registerEntry_Success() {
        when(carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE))
                .thenReturn(testCar);
        when(spotService.occupySpot(testCar)).thenReturn(testSpot);
        when(transactionRepository.save(any(ParkingTransaction.class))).thenReturn(testTransaction);
        when(transactionRepository.existsByCarAndExitTimeIsNull(testCar)).thenReturn(false);

        ParkingTransaction result = entryService.registerEntry(testRequest);

        assertNotNull(result);
        assertEquals(testCar, result.getCar());
        assertEquals(testSpot, result.getSpot());
        assertNotNull(result.getEntryTime());
        assertNull(result.getExitTime());
        verify(transactionRepository).save(any(ParkingTransaction.class));
    }

    @Test
    void registerEntry_NoAvailableSpots_ThrowsException() {
        when(carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE))
                .thenReturn(testCar);
        when(spotService.occupySpot(testCar))
                .thenThrow(new NoAvailableSpotsException("No available spots"));

        assertThrows(NoAvailableSpotsException.class, () ->
            entryService.registerEntry(testRequest)
        );

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void registerEntry_CarAlreadyParked_ThrowsException() {
        when(carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE))
                .thenReturn(testCar);
        when(transactionRepository.existsByCarAndExitTimeIsNull(testCar)).thenReturn(true);

        assertThrows(CarAlreadyParkedException.class, () ->
            entryService.registerEntry(testRequest)
        );

        verify(spotService, never()).occupySpot(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void registerEntry_CarNotParked_ProceedsWithEntry() {
        when(carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE))
                .thenReturn(testCar);
        when(transactionRepository.existsByCarAndExitTimeIsNull(testCar)).thenReturn(false);
        when(spotService.occupySpot(testCar)).thenReturn(testSpot);
        when(transactionRepository.save(any(ParkingTransaction.class))).thenReturn(testTransaction);

        ParkingTransaction result = entryService.registerEntry(testRequest);

        assertNotNull(result);
        assertEquals(testCar, result.getCar());
        assertEquals(testSpot, result.getSpot());
        assertNotNull(result.getEntryTime());
        assertNull(result.getExitTime());
        verify(transactionRepository).save(any(ParkingTransaction.class));
    }
} 