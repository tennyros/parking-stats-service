package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.exception.CarNotFoundException;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.ParkingExitService;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gitverse.testcakes.parkings.service.CarService;

import java.time.LocalDateTime;

/**
 * Implementation of the ParkingExitService interface.
 * Handles the business logic for processing car exits from the parking lot.
 *
 * <p>This service is responsible for:
 * <ul>
 *     <li>Finding active parking transactions</li>
 *     <li>Recording exit times</li>
 *     <li>Releasing parking spots</li>
 *     <li>Updating car exit information</li>
 * </ul>
 *
 * @author vadim_23
 * @see ParkingExitService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingExitServiceImpl implements ParkingExitService {

    private final ParkingTransactionRepository transactionRepo;
    private final ParkingSpotService spotService;
    private final CarService carService;

    /**
     * Processes a car's exit from the parking lot.
     * This method performs the following operations:
     * <ol>
     *     <li>Finds the active parking transaction for the given license plate</li>
     *     <li>Records the exit time</li>
     *     <li>Releases the parking spot</li>
     *     <li>Updates the car's exit time</li>
     *     <li>Saves the completed transaction</li>
     * </ol>
     *
     * @param licensePlate the license plate of the exiting car
     * @return the completed parking transaction
     * @throws CarNotFoundException if the car is not found or not currently parked
     */
    @Override
    @Transactional
    public ParkingTransaction processExit(String licensePlate) {
        ParkingTransaction transaction = findActiveTransaction(licensePlate);
        LocalDateTime exitTime = LocalDateTime.now();
        
        transaction.setExitTime(exitTime);
        spotService.releaseSpot(transaction.getSpot().getId());
        carService.updateCarExitTime(licensePlate, exitTime);
        
        log.debug("Car {} exited from spot {} at {}", 
            licensePlate, transaction.getSpot().getId(), exitTime);
        
        return transactionRepo.save(transaction);
    }

    private ParkingTransaction findActiveTransaction(String licensePlate) {
        return transactionRepo.findActiveTransaction(licensePlate)
                .orElseThrow(() -> {
                    log.warn("No active parking found for car {}", licensePlate);
                    return new CarNotFoundException(String.format(
                            "No active parking found for car with %s license plate", licensePlate));
                });
    }
}