package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.enums.CarType;

import java.time.LocalDateTime;

/**
 * Service for managing car-related operations in the parking system.
 * This service handles car registration, lookup, and exit time updates.
 */
public interface CarService {

    /**
     * Finds an existing car by license plate or registers a new one if not found.
     * This method ensures that each car is registered only once in the system,
     * maintaining data consistency for parking transactions and reporting.
     *
     * @param licensePlate the license plate of the car to find or register
     * @param type the type of the car (e.g., PASSENGER, TRUCK)
     * @return the found or newly registered car entity
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    Car findOrRegisterCar(String licensePlate, CarType type);

    /**
     * Updates the exit time for a car in the system.
     * This method is typically called when a car leaves the parking lot
     * to record the exit time for reporting and billing purposes.
     *
     * @param licensePlate the license plate of the car to update
     * @param exitTime the time when the car exited the parking lot
     * @throws IllegalArgumentException if licensePlate is null or empty, or if exitTime is null
     */
    void updateCarExitTime(String licensePlate, LocalDateTime exitTime);

}
