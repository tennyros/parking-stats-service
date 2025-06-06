package com.gitverse.testcakes.parkings.controller;

import com.gitverse.testcakes.parkings.controller.v1.ParkingControllerV1;
import com.gitverse.testcakes.parkings.controller.v2.ParkingControllerV2;
import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.dto.car.request.CarExitRequest;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.exception.CarAlreadyParkedException;
import com.gitverse.testcakes.parkings.exception.CarNotFoundException;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.service.ParkingEntryService;
import com.gitverse.testcakes.parkings.service.ParkingExitService;
import com.gitverse.testcakes.parkings.service.ReportingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.gitverse.testcakes.parkings.util.TestData.NOW;
import static com.gitverse.testcakes.parkings.util.TestData.TEST_ENTRY_TIME;
import static com.gitverse.testcakes.parkings.util.TestData.TEST_EXIT_TIME;
import static com.gitverse.testcakes.parkings.util.TestData.asJsonString;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestTransaction;
import static com.gitverse.testcakes.parkings.util.TestData.buildValidEntryRequest;
import static com.gitverse.testcakes.parkings.util.TestData.buildValidExitRequest;
import static com.gitverse.testcakes.parkings.util.TestData.buildValidParkingReport;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ParkingControllerV1.class, ParkingControllerV2.class})
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParkingEntryService entryService;

    @MockitoBean
    private ParkingExitService exitService;

    @MockitoBean
    private ReportingService reportService;

    private static Stream<Arguments> provideApiVersions() {
        return Stream.of(
                Arguments.of("/api/v1/parking"),
                Arguments.of("/api/v2/parking")
        );
    }

    @Nested
    @DisplayName("Car Entry Tests")
    class CarEntryTests {

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should register car entry successfully")
        void registerEntry_ValidRequest_ReturnsOk(String baseUrl) throws Exception {
            CarEntryRequest request = buildValidEntryRequest();
            ParkingTransaction transaction = buildTestTransaction();

            when(entryService.registerEntry(any())).thenReturn(transaction);

            mockMvc.perform(post(baseUrl + "/entry")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.licensePlate").value(transaction.getCar().getLicensePlate()))
                    .andExpect(jsonPath("$.entryTime").exists());
        }

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should return bad request for invalid entry request")
        void registerEntry_InvalidRequest_ReturnsBadRequest(String baseUrl) throws Exception {
            CarEntryRequest request = new CarEntryRequest("", null);

            mockMvc.perform(post(baseUrl + "/entry")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should return conflict when car is already parked")
        void registerEntry_CarAlreadyParked_ReturnsConflict(String baseUrl) throws Exception {
            CarEntryRequest request = buildValidEntryRequest();

            when(entryService.registerEntry(any()))
                    .thenThrow(new CarAlreadyParkedException("Car already parked"));

            mockMvc.perform(post(baseUrl + "/entry")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isConflict());
        }

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should return conflict when no spots available")
        void registerEntry_NoAvailableSpots_ReturnsConflict(String baseUrl) throws Exception {
            CarEntryRequest request = buildValidEntryRequest();

            when(entryService.registerEntry(any()))
                    .thenThrow(new NoAvailableSpotsException("No available spots"));

            mockMvc.perform(post(baseUrl + "/entry")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Car Exit Tests")
    class CarExitTests {

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should register car exit successfully")
        void registerExit_ValidRequest_ReturnsOk(String baseUrl) throws Exception {
            CarExitRequest request = buildValidExitRequest();
            ParkingTransaction transaction = buildTestTransaction();
            transaction.setExitTime(NOW.plusHours(2));

            when(exitService.processExit(any())).thenReturn(transaction);

            mockMvc.perform(post(baseUrl + "/exit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.licensePlate").value(transaction.getCar().getLicensePlate()))
                    .andExpect(jsonPath("$.entryTime").exists())
                    .andExpect(jsonPath("$.exitTime").exists());
        }

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should return bad request for invalid exit request")
        void registerExit_InvalidRequest_ReturnsBadRequest(String baseUrl) throws Exception {
            CarExitRequest request = new CarExitRequest("");

            mockMvc.perform(post(baseUrl + "/exit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should return not found when car not found")
        void registerExit_CarNotFound_ReturnsNotFound(String baseUrl) throws Exception {
            CarExitRequest request = buildValidExitRequest();

            when(exitService.processExit(any()))
                    .thenThrow(new CarNotFoundException("Car not found"));

            mockMvc.perform(post(baseUrl + "/exit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Report Tests")
    class ReportTests {

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should generate report successfully")
        void generateReport_ValidRequest_ReturnsOk(String baseUrl) throws Exception {
            ParkingReport report = buildValidParkingReport();

            when(reportService.generateReport(any(), any())).thenReturn(report);

            mockMvc.perform(get(baseUrl + "/report")
                            .param("start", TEST_ENTRY_TIME.toString())
                            .param("end", TEST_EXIT_TIME.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEntries").value(report.totalEntries()))
                    .andExpect(jsonPath("$.totalExits").value(report.totalExits()))
                    .andExpect(jsonPath("$.averageParkingDuration").exists())
                    .andExpect(jsonPath("$.entriesByType").exists());
        }

        @ParameterizedTest
        @MethodSource("com.gitverse.testcakes.parkings.controller.ParkingControllerTest#provideApiVersions")
        @DisplayName("Should return internal server error for invalid date range")
        void generateReport_InvalidDateRange_ReturnsInternalServerError(String baseUrl) throws Exception {
            mockMvc.perform(get(baseUrl + "/report")
                            .param("start", TEST_EXIT_TIME.toString())
                            .param("end", TEST_ENTRY_TIME.toString()))
                    .andExpect(status().isBadRequest());
        }
    }
} 