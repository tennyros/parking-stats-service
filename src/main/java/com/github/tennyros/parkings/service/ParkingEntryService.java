package com.github.tennyros.parkings.service;

import com.github.tennyros.parkings.dto.car.request.CarEntryRequest;
import com.github.tennyros.parkings.entity.ParkingTransaction;
import com.github.tennyros.parkings.exception.CarAlreadyParkedException;
import com.github.tennyros.parkings.exception.NoAvailableSpotsException;

/**
 * Service for managing car entry operations in the parking system.
 * This service handles the process of registering cars entering the parking lot,
 * including spot allocation and transaction creation.
 */
public interface ParkingEntryService {

    /**
     * Registers a car entering the parking lot.
     * This method performs the following operations:
     * 1. Normalizes the license plate
     * 2. Finds or registers the car in the system
     * 3. Checks if the car is already parked
     * 4. Allocates an appropriate parking spot
     * 5. Creates a parking transaction
     *
     * @param request the car entry request containing license plate and car type
     * @return the created parking transaction
     * @throws CarAlreadyParkedException if the car is already parked in the lot
     * @throws IllegalArgumentException if the request is null or contains invalid data
     * @throws NoAvailableSpotsException if no suitable parking spots are available
     */
    ParkingTransaction registerEntry(CarEntryRequest request);
}
