package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.exception.CarNotFoundException;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.CarService;
import com.gitverse.testcakes.parkings.service.ParkingExitService;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of the ParkingExitService interface.
 *
 * @see ParkingExitService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingExitServiceImpl implements ParkingExitService {

    private final ParkingTransactionRepository transactionRepository;
    private final ParkingSpotService spotService;
    private final CarService carService;

    @Override
    @Transactional
    public ParkingTransaction processExit(String licensePlate) {
        ParkingTransaction transaction = transactionRepository.findActiveTransaction(licensePlate)
                .orElseThrow(() -> new CarNotFoundException(
                        String.format("No active parking session found for car %s", licensePlate)));

        LocalDateTime exitTime = LocalDateTime.now();
        transaction.setExitTime(exitTime);
        spotService.freeSpot(transaction.getSpot().getId());
        carService.updateCarExitTime(licensePlate, exitTime);
        
        log.debug("Car {} exited from spot {}", licensePlate, transaction.getSpot().getId());
        return transactionRepository.save(transaction);
    }
}