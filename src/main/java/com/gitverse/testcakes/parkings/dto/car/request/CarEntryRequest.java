package com.gitverse.testcakes.parkings.dto.car.request;

import com.gitverse.testcakes.parkings.entity.enums.CarType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for car entry request.
 * Validates Russian license plate numbers according to GOST R 50577-2018.
 * Supports various types of license plates:<p>
 * - Regular cars (e.g., А123ВЕ77)<p>
 * - Motorcycles (e.g., 1234АА77)<p>
 * - Taxis and buses (e.g., АО36578)<p>
 * - Trailers (e.g., АН733147)<p>
 * - Diplomatic vehicles (e.g., 123CD12377)<p>
 * - And other special types
 *
 * @param licensePlate The license plate number of the vehicle
 * @param type The type of the vehicle
 */
public record CarEntryRequest(
        @NotBlank(message = "License plate cannot be empty")
        @Pattern(
        regexp = LicensePlatePatterns.RUSSIAN_LICENSE_PLATE,
        message = "Invalid license plate format. Must follow Russian GOST R 50577-2018 standard"
        )
        String licensePlate,
        @NotNull(message = "Car type cannot be null")
        CarType type
) {}
