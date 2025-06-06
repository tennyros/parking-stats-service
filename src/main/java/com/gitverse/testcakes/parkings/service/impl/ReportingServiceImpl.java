package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the ReportingService interface.
 *
 * @see ReportingService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final ParkingTransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
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
