package com.gitverse.testcakes.parkings.dto.car.request;

import jakarta.validation.constraints.NotBlank;

public record CarExitRequest(

    @NotBlank(message = "License plate cannot be empty")
    String licensePlate

) {}