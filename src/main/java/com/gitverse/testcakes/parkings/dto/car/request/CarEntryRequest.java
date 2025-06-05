package com.gitverse.testcakes.parkings.dto.car.request;

import com.gitverse.testcakes.parkings.entity.enums.CarType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarEntryRequest(

        @NotBlank String licensePlate,
        @NotNull CarType type

) {}
