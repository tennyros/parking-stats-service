package com.github.tennyros.parkings.service.impl;

import com.github.tennyros.parkings.entity.ParkingTransaction;
import com.github.tennyros.parkings.exception.CarNotFoundException;
import com.github.tennyros.parkings.repository.ParkingTransactionRepository;
import com.github.tennyros.parkings.service.CarService;
import com.github.tennyros.parkings.service.ParkingSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.github.tennyros.parkings.util.TestData.TEST_LICENSE_PLATE;
import static com.github.tennyros.parkings.util.TestData.buildTestTransaction;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingExitServiceImplTest {

    @Mock
    private ParkingTransactionRepository transactionRepository;

    @Mock
    private ParkingSpotService spotService;

    @Mock
    private CarService carService;

    @InjectMocks
    private ParkingExitServiceImpl exitService;

    private ParkingTransaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = buildTestTransaction();
    }

    @Test
    void processExit_Success() {
        when(transactionRepository.findActiveTransaction(TEST_LICENSE_PLATE))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(ParkingTransaction.class)))
                .thenReturn(testTransaction);

        ParkingTransaction result = exitService.processExit(TEST_LICENSE_PLATE);

        assertNotNull(result);
        assertNotNull(result.getExitTime());
        verify(spotService).freeSpot(testTransaction.getSpot().getId());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void processExit_NoActiveTransaction_ThrowsException() {
        when(transactionRepository.findActiveTransaction(TEST_LICENSE_PLATE))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () ->
            exitService.processExit(TEST_LICENSE_PLATE)
        );

        verify(spotService, never()).freeSpot(any());
        verify(transactionRepository, never()).save(any());
    }
} 