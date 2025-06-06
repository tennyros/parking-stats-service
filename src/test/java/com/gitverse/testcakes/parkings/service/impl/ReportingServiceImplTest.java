package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.gitverse.testcakes.parkings.util.TestData.TEST_ENTRY_TIME;
import static com.gitverse.testcakes.parkings.util.TestData.TEST_EXIT_TIME;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestCar;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestSpot;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestTransaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingServiceImplTest {

    @Mock
    private ParkingTransactionRepository transactionRepository;

    @InjectMocks
    private ReportingServiceImpl reportingService;

    private List<ParkingTransaction> testTransactions;

    @BeforeEach
    void setUp() {
        Car passengerCar = buildTestCar("A123BC", CarType.PASSENGER);
        Car truckCar = buildTestCar("B456DE", CarType.TRUCK);
        Car motorcycleCar = buildTestCar("C789FG", CarType.MOTORCYCLE);
        Car specialCar = buildTestCar("D012HI", CarType.SPECIAL);

        ParkingSpot passengerSpot = buildTestSpot(1L, CarType.PASSENGER, false);
        ParkingSpot truckSpot = buildTestSpot(2L, CarType.TRUCK, false);
        ParkingSpot motorcycleSpot = buildTestSpot(3L, CarType.MOTORCYCLE, false);
        ParkingSpot specialSpot = buildTestSpot(4L, CarType.SPECIAL, false);

        LocalDateTime startTime = TEST_ENTRY_TIME;
        LocalDateTime endTime = TEST_EXIT_TIME;

        ParkingTransaction passengerTransaction = buildTestTransaction(
            passengerCar, passengerSpot, startTime, endTime);
        ParkingTransaction truckTransaction = buildTestTransaction(
            truckCar, truckSpot, startTime, endTime);
        ParkingTransaction motorcycleTransaction = buildTestTransaction(
            motorcycleCar, motorcycleSpot, startTime, endTime);
        ParkingTransaction specialTransaction = buildTestTransaction(
            specialCar, specialSpot, startTime, endTime);

        testTransactions = Arrays.asList(
            passengerTransaction,
            truckTransaction,
            motorcycleTransaction,
            specialTransaction
        );
    }

    @Test
    void generateReport_Success() {
        when(transactionRepository.findAllByEntryTimeBetween(any(), any()))
                .thenReturn(testTransactions);

        ParkingReport report = reportingService.generateReport(
            TEST_ENTRY_TIME,
            TEST_EXIT_TIME
        );

        assertNotNull(report);
        assertEquals(4, report.totalEntries());
        assertEquals(4, report.totalExits());
        assertEquals(Duration.ofDays(2), report.averageParkingDuration());
        assertEquals(1L, report.entriesByType().get(CarType.PASSENGER));
        assertEquals(1L, report.entriesByType().get(CarType.TRUCK));
        assertEquals(1L, report.entriesByType().get(CarType.MOTORCYCLE));
        assertEquals(1L, report.entriesByType().get(CarType.SPECIAL));
    }

    @Test
    void generateReport_WithActiveTransactions() {
        // Create transactions with some having null exit time
        Car activeCar = buildTestCar("E345JK", CarType.PASSENGER);
        Car completedCar = buildTestCar("F678LM", CarType.PASSENGER);
        
        ParkingSpot activeSpot = buildTestSpot(5L, CarType.PASSENGER, false);
        ParkingSpot completedSpot = buildTestSpot(6L, CarType.PASSENGER, false);

        ParkingTransaction activeTransaction = buildTestTransaction(
            activeCar, activeSpot, TEST_ENTRY_TIME, null);
        ParkingTransaction completedTransaction = buildTestTransaction(
            completedCar, completedSpot, TEST_ENTRY_TIME, TEST_EXIT_TIME);

        List<ParkingTransaction> mixedTransactions = Arrays.asList(
            activeTransaction,
            completedTransaction
        );

        when(transactionRepository.findAllByEntryTimeBetween(any(), any()))
                .thenReturn(mixedTransactions);

        ParkingReport report = reportingService.generateReport(
            TEST_ENTRY_TIME,
            TEST_EXIT_TIME
        );

        assertNotNull(report);
        assertEquals(2, report.totalEntries());
        assertEquals(1, report.totalExits());
        assertEquals(Duration.ofDays(2), report.averageParkingDuration());
        assertEquals(2L, report.entriesByType().get(CarType.PASSENGER));
    }

    @Test
    void generateReport_InvalidDateRange_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            reportingService.generateReport(
                TEST_EXIT_TIME,
                TEST_ENTRY_TIME
            )
        );
    }
} 