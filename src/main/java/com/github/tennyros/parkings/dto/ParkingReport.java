package com.github.tennyros.parkings.dto;

import com.github.tennyros.parkings.entity.enums.CarType;
import jakarta.validation.constraints.Positive;

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

        @Positive int totalEntries,
        @Positive int totalExits,
        Duration averageParkingDuration,
        Map<CarType, Long> entriesByType

) {}