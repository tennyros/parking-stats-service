package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the reporting service for parking management.
 * Provides functionality to generate comprehensive reports about parking activities
 * within specified time periods.
 *
 * @author vadim_23
 */
@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final ParkingTransactionRepository transactionRepository;

    /**
     * Generates a comprehensive parking report for the specified time period.<p>
     * The report includes:<p>
     * - Total number of cars parked<p>
     * - Total duration of all parking sessions<p>
     * - Average parking duration<p>
     * - List of all parking transactions
     *
     * @param start the start time of the report period (inclusive)
     * @param end the end time of the report period (inclusive)
     * @return ParkingReport containing aggregated statistics and transaction details
     * @throws IllegalArgumentException if start time is after end time
     */
    @Override
    @Transactional
    public ParkingReport generateReport(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        List<ParkingTransaction> transactions = transactionRepository.findAllByEntryTimeBetween(start, end);

        Map<CarType, Long> entriesByType = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCar().getType(),
                        Collectors.counting()
                ));

        double avgSeconds = transactions.stream()
                .filter(t -> t.getExitTime() != null)
                .mapToLong(t -> Duration.between(t.getEntryTime(), t.getExitTime()).getSeconds())
                .average()
                .orElse(0);

        return new ParkingReport(
                transactions.size(),
                (int) transactions.stream().filter(t -> t.getExitTime() != null).count(),
                Duration.ofSeconds((long) avgSeconds),
                entriesByType
        );
    }
}
