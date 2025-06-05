package com.gitverse.testcakes.parkings.dto.car.request;

import jakarta.validation.constraints.NotBlank;

public record CarExitRequest(

    @NotBlank String licensePlate

) {}