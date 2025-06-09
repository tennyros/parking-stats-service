package com.github.tennyros.parkings.dto.car.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for car exit response.<p>
 * Contains information about a completed parking session.
 *
 * @author vadim_23
 */
@Schema(description = "Response object for car exit registration")
public record CarExitResponse(

    @Schema(description = "License plate of the exiting car", example = "A123BC")
    String licensePlate,

    @Schema(description = "Time when the car entered the parking lot", example = "2024-03-15T10:30:00")
    LocalDateTime entryTime,

    @Schema(description = "Time when the car exited the parking lot", example = "2024-03-15T12:45:00")
    LocalDateTime exitTime,

    @Schema(description = "Total duration of the parking session", example = "PT2H15M")
    Duration duration

) {}