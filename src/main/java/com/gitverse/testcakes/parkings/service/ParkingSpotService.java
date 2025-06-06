package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;

/**
 * Service for managing parking spots in the parking system.
 * This service handles spot allocation, deallocation, and status tracking
 * for different types of parking spots (passenger, truck).
 */
public interface ParkingSpotService {

    /**
     * Occupies a parking spot suitable for the specified car type.
     * The method will find an available spot that matches the car type requirements
     * and mark it as occupied.
     *
     * @param carType the type of car that needs a parking spot
     * @return the occupied parking spot
     * @throws NoAvailableSpotsException if no suitable spots are available for the car type
     */
    ParkingSpot occupySpot(CarType carType);

    /**
     * Frees up a previously occupied parking spot.
     * This method should be called when a car leaves the parking spot
     * to make it available for other cars.
     *
     * @param spotId the ID of the spot to free
     * @throws NoAvailableSpotsException if the spot with the given ID doesn't exist
     */
    void freeSpot(Long spotId);

}
