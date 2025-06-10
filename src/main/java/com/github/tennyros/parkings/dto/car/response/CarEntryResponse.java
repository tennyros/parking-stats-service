package com.github.tennyros.parkings.dto.car.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for car entry response.<p>
 * Contains information about a successfully registered car entry.
 *
 * @author vadim_23
 */
@Schema(description = "Response object for car entry registration")
public record CarEntryResponse(

    @Schema(description = "License plate of the parked car", example = "A123BC")
    String licensePlate,

    @Schema(description = "Time when the car entered the parking lot", example = "2024-03-15T10:30:00")
    LocalDateTime entryTime

) {}