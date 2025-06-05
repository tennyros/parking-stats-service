package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.entity.Car;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingServiceImplTest {

    @Mock
    private ParkingTransactionRepository transactionRepository;

    @InjectMocks
    private ReportingServiceImpl reportingService;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ParkingTransaction> testTransactions;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now().minusHours(24);
        endTime = LocalDateTime.now();

        Car car1 = new Car();
        car1.setLicensePlate("A123BC");
        car1.setType(CarType.PASSENGER);

        Car car2 = new Car();
        car2.setLicensePlate("B456DE");
        car2.setType(CarType.TRUCK);

        ParkingTransaction transaction1 = ParkingTransaction.builder()
                .car(car1)
                .entryTime(startTime.plusHours(1))
                .exitTime(startTime.plusHours(3))
                .build();

        ParkingTransaction transaction2 = ParkingTransaction.builder()
                .car(car2)
                .entryTime(startTime.plusHours(2))
                .exitTime(startTime.plusHours(4))
                .build();

        testTransactions = Arrays.asList(transaction1, transaction2);
    }

    @Test
    void generateReport_Success() {
        when(transactionRepository.findAllByEntryTimeBetween(startTime, endTime))
                .thenReturn(testTransactions);

        ParkingReport report = reportingService.generateReport(startTime, endTime);

        assertNotNull(report);
        assertEquals(2, report.totalEntries());
        assertEquals(2, report.totalExits());
        assertEquals(Duration.ofHours(2), report.averageParkingDuration());
        
        Map<CarType, Long> entriesByType = report.entriesByType();
        assertEquals(1, entriesByType.get(CarType.PASSENGER));
        assertEquals(1, entriesByType.get(CarType.TRUCK));
    }

    @Test
    void generateReport_InvalidDateRange() {
        assertThrows(IllegalArgumentException.class,
            () -> reportingService.generateReport(endTime, startTime));
        verify(transactionRepository, never()).findAllByEntryTimeBetween(any(), any());
    }

    @Test
    void generateReport_NoTransactions() {
        when(transactionRepository.findAllByEntryTimeBetween(startTime, endTime))
                .thenReturn(List.of());

        ParkingReport report = reportingService.generateReport(startTime, endTime);

        assertNotNull(report);
        assertEquals(0, report.totalEntries());
        assertEquals(0, report.totalExits());
        assertEquals(Duration.ZERO, report.averageParkingDuration());
        assertTrue(report.entriesByType().isEmpty());
    }

    @Test
    void generateReport_WithActiveTransactions() {
        Car car3 = new Car();
        car3.setLicensePlate("C789FG");
        car3.setType(CarType.PASSENGER);

        ParkingTransaction activeTransaction = ParkingTransaction.builder()
                .car(car3)
                .entryTime(startTime.plusHours(5))
                .build();

        List<ParkingTransaction> transactionsWithActive = Arrays.asList(
                testTransactions.get(0),
                testTransactions.get(1),
                activeTransaction
        );

        when(transactionRepository.findAllByEntryTimeBetween(startTime, endTime))
                .thenReturn(transactionsWithActive);

        ParkingReport report = reportingService.generateReport(startTime, endTime);

        assertNotNull(report);
        assertEquals(3, report.totalEntries());
        assertEquals(2, report.totalExits());
        assertEquals(Duration.ofHours(2), report.averageParkingDuration());
        
        Map<CarType, Long> entriesByType = report.entriesByType();
        assertEquals(2, entriesByType.get(CarType.PASSENGER));
        assertEquals(1, entriesByType.get(CarType.TRUCK));
    }
} 