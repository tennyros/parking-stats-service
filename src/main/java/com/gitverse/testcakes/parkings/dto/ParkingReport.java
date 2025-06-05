package com.gitverse.testcakes.parkings.dto;

import com.gitverse.testcakes.parkings.entity.enums.CarType;

import java.time.Duration;
import java.util.Map;

public record ParkingReport(

        int totalEntries,
        int totalExits,
        Duration averageParkingDuration,
        Map<CarType, Long> entriesByType

) {}