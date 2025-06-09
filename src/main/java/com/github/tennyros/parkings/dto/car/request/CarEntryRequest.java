package com.github.tennyros.parkings.dto.car.request;

import com.github.tennyros.parkings.entity.enums.CarType;
import com.github.tennyros.parkings.validation.LicensePlatePatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for car entry request.<p>
 * Contains information required to register a car's entry into the parking lot.
 *
 * @author vadim_23
 */
@Schema(description = "Request object for car entry registration")
public record CarEntryRequest(
    
    @Schema(description = "License plate of the car", example = "A123BC")
    @NotBlank(message = "License plate is required")
    @Pattern(
        regexp = LicensePlatePatterns.RUSSIAN_LICENSE_PLATE,
        message = "Invalid license plate format. Must follow Russian GOST R 50577-2018 standard"
    )
    String licensePlate,

    @Schema(description = "Type of the car", example = "SEDAN")
    @NotNull(message = "Car type is required")
    CarType type

) {}
