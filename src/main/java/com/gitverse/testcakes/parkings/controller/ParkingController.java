package com.gitverse.testcakes.parkings.controller;

import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.dto.car.request.CarExitRequest;
import com.gitverse.testcakes.parkings.dto.car.response.CarEntryResponse;
import com.gitverse.testcakes.parkings.dto.car.response.CarExitResponse;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.service.ParkingEntryService;
import com.gitverse.testcakes.parkings.service.ReportingService;
import com.gitverse.testcakes.parkings.service.impl.ParkingExitServiceImpl;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parking")
public class ParkingController {

    private final ReportingService reportingService;
    private final ParkingEntryService entryService;
    private final ParkingExitServiceImpl exitService;

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

    @GetMapping("/report")
    public ResponseEntity<ParkingReport> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        ParkingReport report = reportingService.generateReport(start, end);
        return ResponseEntity.ok(
                new ParkingReport(
                        report.totalEntries(),
                        report.totalExits(),
                        report.averageParkingDuration(),
                        report.entriesByType()
                )
        );
    }

}
