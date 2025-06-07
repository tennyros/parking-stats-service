package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.config.BatchConfig;
import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.ReportingService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    private final BatchConfig batchConfig;

    @Override
    @Transactional(readOnly = true)
    public ParkingReport generateReport(LocalDateTime start, LocalDateTime end) {
        validateTimeRange(start, end);

        final ReportStatistics statistics = processInBatches(start, end);

        return buildReport(statistics);
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private ReportStatistics processInBatches(LocalDateTime start, LocalDateTime end) {
        ReportStatistics statistics = new ReportStatistics();
        int page = 0;

        while (true) {
            List<ParkingTransaction> batch = fetchBatch(start, end, page++);
            if (batch.isEmpty()) break;

            processBatch(batch, statistics);
        }

        return statistics;
    }

    private List<ParkingTransaction> fetchBatch(LocalDateTime start, LocalDateTime end, int page) {
        log.debug("Fetching page {} with size {}", page, batchConfig.getSize());
        return transactionRepository.findAllByEntryTimeBetweenWithPagination(
                start,
                end,
                PageRequest.of(page, batchConfig.getSize())
        );
    }

    private void processBatch(List<ParkingTransaction> batch, ReportStatistics statistics) {
        log.debug("Processing batch of {} transactions", batch.size());

        batch.forEach(transaction -> {
            statistics.entriesByType.merge(transaction.getCar().getType(), 1L, Long::sum);
            statistics.processedCount++;

            if (transaction.getExitTime() != null) {
                statistics.totalExits++;
                statistics.totalDurationSeconds += Duration.between(
                        transaction.getEntryTime(),
                        transaction.getExitTime()
                ).getSeconds();
            }
        });
    }

    private ParkingReport buildReport(ReportStatistics statistics) {
        double avgSeconds = statistics.totalExits > 0
                ? (double) statistics.totalDurationSeconds / statistics.totalExits
                : 0;

        log.info("Processed {} transactions in total", statistics.processedCount);

        return new ParkingReport(
                statistics.processedCount,
                (int) statistics.totalExits,
                Duration.ofSeconds((long) avgSeconds),
                statistics.entriesByType
        );
    }

    @Getter
    @NoArgsConstructor
    private static class ReportStatistics {
        private final Map<CarType, Long> entriesByType = new EnumMap<>(CarType.class);
        private int processedCount = 0;
        private long totalExits = 0;
        private long totalDurationSeconds = 0;
    }
}
