package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.dto.ParkingReport;

import java.time.LocalDateTime;

public interface ReportingService {

    ParkingReport generateReport(LocalDateTime start, LocalDateTime end);

}
