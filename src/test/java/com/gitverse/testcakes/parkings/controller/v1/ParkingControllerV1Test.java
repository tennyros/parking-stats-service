package com.gitverse.testcakes.parkings.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.dto.car.request.CarExitRequest;
import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.exception.CarAlreadyParkedException;
import com.gitverse.testcakes.parkings.exception.CarNotFoundException;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.service.ParkingEntryService;
import com.gitverse.testcakes.parkings.service.ParkingExitService;
import com.gitverse.testcakes.parkings.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingControllerV1.class)
class ParkingControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingEntryService entryService;

    @MockitoBean
    private ParkingExitService exitService;

    @MockitoBean
    private ReportingService reportingService;

    private CarEntryRequest validRequest;
    private CarExitRequest validExitRequest;
    private ParkingTransaction testTransaction;
    private ParkingReport testReport;

    @BeforeEach
    void setUp() {
        validRequest = new CarEntryRequest("А123ВС777", CarType.PASSENGER);
        validExitRequest = new CarExitRequest("А123ВС777");

        Car car = new Car();
        car.setLicensePlate("А123ВС777");
        car.setType(CarType.PASSENGER);

        ParkingSpot spot = new ParkingSpot();
        spot.setId(1L);
        spot.setSpotType(CarType.PASSENGER);

        testTransaction = ParkingTransaction.builder()
                .car(car)
                .spot(spot)
                .entryTime(LocalDateTime.now().minusHours(2))
                .exitTime(LocalDateTime.now())
                .build();

        testReport = new ParkingReport(
                10,  // totalEntries
                5,   // totalExits
                Duration.ofHours(2),  // averageParkingDuration
                Map.of(CarType.PASSENGER, 8L, CarType.TRUCK, 2L)  // entriesByType
        );
    }

    @Test
    void registerEntry_Success() throws Exception {
        when(entryService.registerEntry(any(CarEntryRequest.class))).thenReturn(testTransaction);

        mockMvc.perform(post("/api/v1/parking/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("А123ВС777"))
                .andExpect(jsonPath("$.entryTime").exists());
    }

    @Test
    void registerEntry_InvalidRequest() throws Exception {
        CarEntryRequest invalidRequest = new CarEntryRequest("", CarType.PASSENGER);

        mockMvc.perform(post("/api/v1/parking/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerEntry_CarAlreadyParked() throws Exception {
        when(entryService.registerEntry(any(CarEntryRequest.class)))
                .thenThrow(new CarAlreadyParkedException("Car is already parked"));

        mockMvc.perform(post("/api/v1/parking/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Car is already parked"));
    }

    @Test
    void registerEntry_NoAvailableSpots() throws Exception {
        when(entryService.registerEntry(any(CarEntryRequest.class)))
                .thenThrow(new NoAvailableSpotsException("No available spots"));

        mockMvc.perform(post("/api/v1/parking/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No available spots"));
    }

    @Test
    void processExit_Success() throws Exception {
        when(exitService.processExit(anyString())).thenReturn(testTransaction);

        mockMvc.perform(post("/api/v1/parking/exit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("А123ВС777"))
                .andExpect(jsonPath("$.entryTime").exists())
                .andExpect(jsonPath("$.exitTime").exists())
                .andExpect(jsonPath("$.duration").exists());
    }

    @Test
    void processExit_InvalidRequest() throws Exception {
        CarExitRequest invalidRequest = new CarExitRequest("");

        mockMvc.perform(post("/api/v1/parking/exit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processExit_CarNotFound() throws Exception {
        when(exitService.processExit(anyString()))
                .thenThrow(new CarNotFoundException("Car not found"));

        mockMvc.perform(post("/api/v1/parking/exit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExitRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Car not found"));
    }

    @Test
    void generateReport_Success() throws Exception {
        when(reportingService.generateReport(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testReport);

        mockMvc.perform(get("/api/v1/parking/report")
                .param("start", LocalDateTime.now().minusHours(24).toString())
                .param("end", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(10))
                .andExpect(jsonPath("$.totalExits").value(5))
                .andExpect(jsonPath("$.averageParkingDuration").exists())
                .andExpect(jsonPath("$.entriesByType").exists());
    }

    @Test
    void generateReport_InvalidDateRange() throws Exception {
        mockMvc.perform(get("/api/v1/parking/report")
                .param("start", "invalid-date")
                .param("end", LocalDateTime.now().toString()))
                .andExpect(status().isBadRequest());
    }
} 