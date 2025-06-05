package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.exception.CarNotFoundException;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.CarService;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingExitServiceImplTest {

    @Mock
    private ParkingTransactionRepository transactionRepo;

    @Mock
    private ParkingSpotService spotService;

    @Mock
    private CarService carService;

    @InjectMocks
    private ParkingExitServiceImpl exitService;

    private ParkingTransaction testTransaction;
    private ParkingSpot testSpot;
    private static final String TEST_LICENSE_PLATE = "A123BC";

    @BeforeEach
    void setUp() {
        Car testCar = new Car();
        testCar.setLicensePlate(TEST_LICENSE_PLATE);
        testCar.setType(CarType.PASSENGER);

        testSpot = new ParkingSpot();
        testSpot.setId(1L);
        testSpot.setSpotType(CarType.PASSENGER);

        testTransaction = ParkingTransaction.builder()
                .car(testCar)
                .spot(testSpot)
                .entryTime(LocalDateTime.now().minusHours(2))
                .build();
    }

    @Test
    void processExit_Success() {
        when(transactionRepo.findActiveTransaction(TEST_LICENSE_PLATE))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepo.save(any(ParkingTransaction.class)))
                .thenReturn(testTransaction);

        ParkingTransaction result = exitService.processExit(TEST_LICENSE_PLATE);

        assertNotNull(result);
        assertNotNull(result.getExitTime());
        verify(spotService).releaseSpot(testSpot.getId());
        verify(carService).updateCarExitTime(eq(TEST_LICENSE_PLATE), any(LocalDateTime.class));
        verify(transactionRepo).save(testTransaction);
    }

    @Test
    void processExit_CarNotFound() {
        when(transactionRepo.findActiveTransaction(TEST_LICENSE_PLATE))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> exitService.processExit(TEST_LICENSE_PLATE));
        verify(spotService, never()).releaseSpot(any());
        verify(carService, never()).updateCarExitTime(anyString(), any());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    void processExit_SetsCorrectDuration() {
        LocalDateTime entryTime = LocalDateTime.now().minusHours(2);
        testTransaction.setEntryTime(entryTime);
        when(transactionRepo.findActiveTransaction(TEST_LICENSE_PLATE))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepo.save(any(ParkingTransaction.class)))
                .thenReturn(testTransaction);

        ParkingTransaction result = exitService.processExit(TEST_LICENSE_PLATE);

        assertNotNull(result.getExitTime());
        assertTrue(result.getExitTime().isAfter(entryTime));
        assertNotNull(result.getDuration());
        assertEquals(2, result.getDuration().toHours());
    }
} 