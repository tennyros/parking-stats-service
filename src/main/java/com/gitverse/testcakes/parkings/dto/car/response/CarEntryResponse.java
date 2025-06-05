package com.gitverse.testcakes.parkings.dto.car.response;

import java.time.LocalDateTime;

public record CarEntryResponse(

    String licensePlate,
    LocalDateTime entryTime

) {}