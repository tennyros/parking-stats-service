package com.gitverse.testcakes.parkings.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitverse.testcakes.parkings.dto.ParkingReport;
import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.dto.car.request.CarExitRequest;
import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Utility class providing test data and helper methods for tests.
 */
@UtilityClass
public class TestData {
    
    // Common test values
    public static final String TEST_LICENSE_PLATE = "А123ВЕ456";
    public static final CarType TEST_CAR_TYPE = CarType.PASSENGER;
    public static final Long TEST_ID = 1L;
    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final LocalDateTime TEST_ENTRY_TIME = NOW.minusDays(1);
    public static final LocalDateTime TEST_EXIT_TIME = NOW.plusDays(1);

    /**
     * Creates a test car entry request with default values.
     */
    public static CarEntryRequest buildValidEntryRequest() {
        return new CarEntryRequest(TEST_LICENSE_PLATE, TEST_CAR_TYPE);
    }

    /**
     * Creates a test car exit request with default values.
     */
    public static CarExitRequest buildValidExitRequest() {
        return new CarExitRequest(TEST_LICENSE_PLATE);
    }

    /**
     * Creates a test car with default values.
     */
    public static Car buildTestCar() {
        return Car.builder()
                .licensePlate(TEST_LICENSE_PLATE)
                .type(TEST_CAR_TYPE)
                .entryTime(TEST_ENTRY_TIME)
                .build();
    }

    /**
     * Creates a test car with custom values.
     */
    public static Car buildTestCar(String licensePlate, CarType type) {
        return Car.builder()
                .licensePlate(licensePlate)
                .type(type)
                .entryTime(TEST_ENTRY_TIME)
                .build();
    }

    /**
     * Creates a test parking spot with default values.
     */
    public static ParkingSpot buildTestSpot() {
        return ParkingSpot.builder()
                .id(TEST_ID)
                .spotType(TEST_CAR_TYPE)
                .occupied(false)
                .build();
    }

    /**
     * Creates a test parking spot with custom values.
     */
    public static ParkingSpot buildTestSpot(Long id, CarType type, boolean occupied) {
        return ParkingSpot.builder()
                .id(id)
                .spotType(type)
                .occupied(occupied)
                .build();
    }

    /**
     * Creates a test parking transaction with default values.
     */
    public static ParkingTransaction buildTestTransaction() {
        return ParkingTransaction.builder()
                .id(TEST_ID)
                .car(buildTestCar())
                .spot(buildTestSpot())
                .entryTime(NOW)
                .build();
    }

    /**
     * Creates a test parking transaction with custom values.
     */
    public static ParkingTransaction buildTestTransaction(Car car, ParkingSpot spot,
                                                          LocalDateTime entryTime, LocalDateTime exitTime) {
        return ParkingTransaction.builder()
                .car(car)
                .spot(spot)
                .entryTime(entryTime)
                .exitTime(exitTime)
                .build();
    }

    /**
     * Creates a test parking report with default values.
     */
    public static ParkingReport buildValidParkingReport() {
        return new ParkingReport(
                10,
                8,
                Duration.ofHours(2),
                Map.of(TEST_CAR_TYPE, 10L)
        );
    }

    /**
     * Converts a Java object to JSON string for testing purposes.
     *
     * @param obj object to convert to JSON
     * @return JSON string representation of the object
     * @throws RuntimeException if serialization fails
     */
    public static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 