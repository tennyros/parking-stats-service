package com.github.tennyros.parkings.controller.v1;

import com.github.tennyros.parkings.dto.ParkingReport;
import com.github.tennyros.parkings.dto.car.request.CarEntryRequest;
import com.github.tennyros.parkings.dto.car.request.CarExitRequest;
import com.github.tennyros.parkings.dto.car.response.CarEntryResponse;
import com.github.tennyros.parkings.dto.car.response.CarExitResponse;
import com.github.tennyros.parkings.entity.ParkingTransaction;
import com.github.tennyros.parkings.service.ParkingEntryService;
import com.github.tennyros.parkings.service.ParkingExitService;
import com.github.tennyros.parkings.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for managing parking operations (API v1).<p>
 * Provides endpoints for parking entry, exit, and spot management.
 *
 * @author vadim_23
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
@Tag(name = "Parking Management", description = "API for managing parking operations")
public class ParkingControllerV1 {

    private final ParkingEntryService entryService;
    private final ParkingExitService exitService;
    private final ReportingService reportingService;

    /**
     * Process a car's entry into the parking lot.
     *
     * @param request the parking entry request containing car and spot details
     * @return ResponseEntity containing the created parking transaction
     */
    @PostMapping("/entry")
    @Operation(summary = "Register car entry", description = "Registers a car entering the parking lot")
    public ResponseEntity<CarEntryResponse> registerEntry(@Valid @RequestBody CarEntryRequest request) {
        log.info("Processing entry request for car {}", request.licensePlate());
        ParkingTransaction transaction = entryService.registerEntry(request);
        CarEntryResponse response = new CarEntryResponse(
                transaction.getCar().getLicensePlate(),
                transaction.getEntryTime()
        );
        log.debug("Car {} successfully registered at spot {}", request.licensePlate(), transaction.getSpot().getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Process a car's exit from the parking lot.
     *
     * @param request the parking exit request containing car details
     * @return ResponseEntity containing the completed parking transaction
     */
    @PostMapping("/exit")
    @Operation(summary = "Register car exit", description = "Registers a car exiting the parking lot")
    public ResponseEntity<CarExitResponse> registerExit(@Valid @RequestBody CarExitRequest request) {
        log.info("Processing exit request for car {}", request.licensePlate());
        ParkingTransaction transaction = exitService.processExit(request.licensePlate());
        CarExitResponse response = new CarExitResponse(
                transaction.getCar().getLicensePlate(),
                transaction.getEntryTime(),
                transaction.getExitTime(),
                transaction.getDuration()
        );
        log.debug("Car {} successfully exited from spot {}", request.licensePlate(), transaction.getSpot().getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a parking report for the specified time period.
     *
     * @param start the start time of the report period (inclusive)
     * @param end the end time of the report period (inclusive)
     * @return ResponseEntity containing the parking report
     */
    @GetMapping("/report")
    @Operation(summary = "Generate parking report", description = "Generates a report of parking operations for a given date range")
    public ResponseEntity<ParkingReport> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PastOrPresent LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @FutureOrPresent LocalDateTime end) {

        log.info("Generating parking report for period {} to {}", start, end);
        ParkingReport report = reportingService.generateReport(start, end);
        log.debug("Report generated successfully with {} entries and {} exits",
                report.totalEntries(), report.totalExits());
        return ResponseEntity.ok(report);
    }
} 