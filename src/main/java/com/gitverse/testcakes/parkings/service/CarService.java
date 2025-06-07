package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.enums.CarType;

/**
 * Service for managing car-related operations in the parking system.
 * This service handles car registration and lookup.
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

}
