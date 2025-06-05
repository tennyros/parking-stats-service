package com.gitverse.testcakes.parkings.dto.car.response;

import java.time.Duration;
import java.time.LocalDateTime;

public record CarExitResponse(

    String licensePlate,
    LocalDateTime entryTime,
    LocalDateTime exitTime,
    Duration duration

) {}