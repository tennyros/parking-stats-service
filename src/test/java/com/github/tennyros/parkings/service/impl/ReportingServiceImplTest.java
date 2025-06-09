package com.github.tennyros.parkings.service.impl;

import com.github.tennyros.parkings.config.BatchConfig;
import com.github.tennyros.parkings.dto.ParkingReport;
import com.github.tennyros.parkings.entity.Car;
import com.github.tennyros.parkings.entity.ParkingSpot;
import com.github.tennyros.parkings.entity.ParkingTransaction;
import com.github.tennyros.parkings.entity.enums.CarType;
import com.github.tennyros.parkings.repository.ParkingTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.github.tennyros.parkings.util.TestData.TEST_ENTRY_TIME;
import static com.github.tennyros.parkings.util.TestData.TEST_EXIT_TIME;
import static com.github.tennyros.parkings.util.TestData.buildTestCar;
import static com.github.tennyros.parkings.util.TestData.buildTestSpot;
import static com.github.tennyros.parkings.util.TestData.buildTestTransaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingServiceImplTest {

    @Mock
    private ParkingTransactionRepository transactionRepository;

    @Mock
    private BatchConfig batchConfig;

    @InjectMocks
    private ReportingServiceImpl reportingService;

    private static final int BATCH_SIZE = 100;

    @BeforeEach
    void setUp() {
        lenient().when(batchConfig.getSize()).thenReturn(BATCH_SIZE);
    }

    @Nested
    @DisplayName("Basic Report Generation Tests")
    class BasicReportTests {

        @Test
        @DisplayName("Should generate report for single batch of transactions")
        void generateReport_SingleBatch() {
            List<ParkingTransaction> transactions = createTestTransactions();
            when(transactionRepository.findAllByEntryTimeBetweenWithPagination(
                    any(), any(), any(PageRequest.class)))
                    .thenReturn(transactions)
                    .thenReturn(List.of());

            ParkingReport report = reportingService.generateReport(TEST_ENTRY_TIME, TEST_EXIT_TIME);

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
        @DisplayName("Should handle empty result set")
        void generateReport_EmptyResult() {
            when(transactionRepository.findAllByEntryTimeBetweenWithPagination(
                    any(), any(), any(PageRequest.class)))
                    .thenReturn(List.of());

            ParkingReport report = reportingService.generateReport(TEST_ENTRY_TIME, TEST_EXIT_TIME);

            assertNotNull(report);
            assertEquals(0, report.totalEntries());
            assertEquals(0, report.totalExits());
            assertEquals(Duration.ZERO, report.averageParkingDuration());
            assertTrue(report.entriesByType().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception for invalid date range")
        void generateReport_InvalidDateRange() {
            assertThrows(IllegalArgumentException.class, () ->
                reportingService.generateReport(TEST_EXIT_TIME, TEST_ENTRY_TIME)
            );
        }
    }

    @Nested
    @DisplayName("Batch Processing Tests")
    class BatchProcessingTests {

        @Test
        @DisplayName("Should process multiple batches correctly")
        void generateReport_MultipleBatches() {
            List<ParkingTransaction> firstBatch = createTestTransactions();
            List<ParkingTransaction> secondBatch = createTestTransactions();
            
            when(transactionRepository.findAllByEntryTimeBetweenWithPagination(
                    any(), any(), any(PageRequest.class)))
                    .thenReturn(firstBatch)
                    .thenReturn(secondBatch)
                    .thenReturn(List.of());

            ParkingReport report = reportingService.generateReport(TEST_ENTRY_TIME, TEST_EXIT_TIME);

            assertNotNull(report);
            assertEquals(8, report.totalEntries());
            assertEquals(8, report.totalExits());
        }

        @Test
        @DisplayName("Should handle mixed completed and active transactions")
        void generateReport_MixedTransactions() {
            // Given
            Car activeCar = buildTestCar("E345JK", CarType.PASSENGER);
            Car completedCar = buildTestCar("F678LM", CarType.PASSENGER);
            
            ParkingSpot activeSpot = buildTestSpot(5L, CarType.PASSENGER, false);
            ParkingSpot completedSpot = buildTestSpot(6L, CarType.PASSENGER, false);

            ParkingTransaction activeTransaction = buildTestTransaction(
                activeCar, activeSpot, TEST_ENTRY_TIME, null);
            ParkingTransaction completedTransaction = buildTestTransaction(
                completedCar, completedSpot, TEST_ENTRY_TIME, TEST_EXIT_TIME);

            List<ParkingTransaction> transactions = Arrays.asList(
                activeTransaction,
                completedTransaction
            );

            when(transactionRepository.findAllByEntryTimeBetweenWithPagination(
                    any(), any(), any(PageRequest.class)))
                    .thenReturn(transactions)
                    .thenReturn(List.of());

            ParkingReport report = reportingService.generateReport(TEST_ENTRY_TIME, TEST_EXIT_TIME);

            assertNotNull(report);
            assertEquals(2, report.totalEntries());
            assertEquals(1, report.totalExits());
            assertEquals(Duration.ofDays(2), report.averageParkingDuration());
            assertEquals(2L, report.entriesByType().get(CarType.PASSENGER));
        }
    }

    private List<ParkingTransaction> createTestTransactions() {
        Car passengerCar = buildTestCar("A123BC", CarType.PASSENGER);
        Car truckCar = buildTestCar("B456DE", CarType.TRUCK);
        Car motorcycleCar = buildTestCar("C789FG", CarType.MOTORCYCLE);
        Car specialCar = buildTestCar("D012HI", CarType.SPECIAL);

        ParkingSpot passengerSpot = buildTestSpot(1L, CarType.PASSENGER, false);
        ParkingSpot truckSpot = buildTestSpot(2L, CarType.TRUCK, false);
        ParkingSpot motorcycleSpot = buildTestSpot(3L, CarType.MOTORCYCLE, false);
        ParkingSpot specialSpot = buildTestSpot(4L, CarType.SPECIAL, false);

        return Arrays.asList(
            buildTestTransaction(passengerCar, passengerSpot, TEST_ENTRY_TIME, TEST_EXIT_TIME),
            buildTestTransaction(truckCar, truckSpot, TEST_ENTRY_TIME, TEST_EXIT_TIME),
            buildTestTransaction(motorcycleCar, motorcycleSpot, TEST_ENTRY_TIME, TEST_EXIT_TIME),
            buildTestTransaction(specialCar, specialSpot, TEST_ENTRY_TIME, TEST_EXIT_TIME)
        );
    }
} 