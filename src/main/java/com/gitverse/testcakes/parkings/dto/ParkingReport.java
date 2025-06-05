package com.gitverse.testcakes.parkings.dto;

import com.gitverse.testcakes.parkings.entity.enums.CarType;

import java.time.Duration;
import java.util.Map;

/**
 * Represents a parking report containing statistics about parking operations.
 * This record is used to aggregate and present parking data for a specific time period.
 *
 * @param totalEntries The total number of cars that entered the parking lot during the report period
 * @param totalExits The total number of cars that exited the parking lot during the report period
 * @param averageParkingDuration The average time cars spent in the parking lot during the report period
 * @param entriesByType A map containing the number of entries for each car type during the report period
 */
public record ParkingReport(
        int totalEntries,
        int totalExits,
        Duration averageParkingDuration,
        Map<CarType, Long> entriesByType
) {}