package com.gitverse.testcakes.parkings.controller.v2;

import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.dto.car.request.CarExitRequest;
import com.gitverse.testcakes.parkings.dto.car.response.CarEntryResponse;
import com.gitverse.testcakes.parkings.dto.car.response.CarExitResponse;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.service.ParkingEntryService;
import com.gitverse.testcakes.parkings.service.ParkingExitService;
import com.gitverse.testcakes.parkings.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * REST controller for managing parking operations (API v2).
 * Provides enhanced endpoints for parking entry, exit, and spot management.
 * Includes additional features and improved response formats.
 *
 * @author vadim_23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/parking")
@Tag(name = "Parking Management V2", description = "Enhanced APIs for managing parking operations (Version 2)")
public class ParkingControllerV2 {

    private final ReportingService reportingService;
    private final ParkingEntryService entryService;
    private final ParkingExitService exitService;

    /**
     * Process a car's entry into the parking lot with enhanced validation and response.
     *
     * @param request the parking entry request containing car and spot details
     * @return ResponseEntity containing the created parking transaction with additional details
     */
    @Operation(
        summary = "Process car entry (V2)",
        description = "Registers a car's entry into the parking lot with enhanced validation and spot assignment"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Car successfully parked",
            content = @Content(schema = @Schema(implementation = CarEntryResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "No available parking spot found"),
        @ApiResponse(responseCode = "409", description = "Car is already parked")
    })
    @PostMapping("/entry")
    public ResponseEntity<CarEntryResponse> registerEntry(@RequestBody @Valid CarEntryRequest request) {
        ParkingTransaction transaction = entryService.registerEntry(request);
        return ResponseEntity.ok(
                new CarEntryResponse(
                        transaction.getCar().getLicensePlate(),
                        transaction.getEntryTime()
                )
        );
    }

    /**
     * Process a car's exit from the parking lot with enhanced response.
     *
     * @param request the parking exit request containing car details
     * @return ResponseEntity containing the completed parking transaction with additional details
     */
    @Operation(
        summary = "Process car exit (V2)",
        description = "Registers a car's exit from the parking lot with enhanced response format"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Car successfully exited",
            content = @Content(schema = @Schema(implementation = CarExitResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Car not found or not parked")
    })
    @PostMapping("/exit")
    public ResponseEntity<CarExitResponse> processExit(@RequestBody @Valid CarExitRequest request) {
        ParkingTransaction transaction = exitService.processExit(request.licensePlate());
        return ResponseEntity.ok(
                new CarExitResponse(
                        transaction.getCar().getLicensePlate(),
                        transaction.getEntryTime(),
                        transaction.getExitTime(),
                        transaction.getDuration()
                )
        );
    }

    /**
     * Generates an enhanced parking report for the specified time period.
     * Includes additional statistics and metrics.
     *
     * @param start the start time of the report period (inclusive)
     * @param end the end time of the report period (inclusive)
     * @return ResponseEntity containing the enhanced parking report
     */
    @Operation(
        summary = "Generate enhanced parking report (V2)",
        description = "Generates a comprehensive report with additional statistics and metrics"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Report generated successfully",
            content = @Content(schema = @Schema(implementation = ParkingReport.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    @GetMapping("/report")
    public ResponseEntity<ParkingReport> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        ParkingReport report = reportingService.generateReport(start, end);
        return ResponseEntity.ok(report);
    }
} 