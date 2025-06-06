package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.exception.CarNotFoundException;

/**
 * Service for managing car exit operations in the parking system.
 * This service handles the process of registering cars leaving the parking lot,
 * including spot deallocation and transaction completion.
 */
public interface ParkingExitService {

    /**
     * Processes a car's exit from the parking lot.
     * This method performs the following operations:
     * 1. Finds the active parking transaction for the car
     * 2. Updates the exit time
     * 3. Frees up the parking spot
     * 4. Updates the car's exit time
     *
     * @param licensePlate the license plate of the car exiting
     * @return the completed parking transaction
     * @throws CarNotFoundException if no active parking session is found for the car
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    ParkingTransaction processExit(String licensePlate);

}
