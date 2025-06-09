package com.github.tennyros.parkings.service;

import com.github.tennyros.parkings.dto.ParkingReport;

import java.time.LocalDateTime;

/**
 * Service for generating parking reports and statistics.
 * This service provides functionality to analyze parking usage patterns,
 * occupancy rates, and other metrics for specified time periods.
 */
public interface ReportingService {

    /**
     * Generates a comprehensive parking report for the specified time period.
     * The report includes:
     * - Total number of entries and exits
     * - Average parking duration
     * - Occupancy rates
     * - Statistics by car type
     * - Revenue information (if applicable)
     *
     * @param start the start of the reporting period (inclusive)
     * @param end the end of the reporting period (inclusive)
     * @return a detailed parking report for the specified period
     * @throws IllegalArgumentException if start is after end, or if either date is null
     */
    ParkingReport generateReport(LocalDateTime start, LocalDateTime end);

}
