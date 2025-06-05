package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.exception.CarNotFoundException;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.ParkingExitService;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gitverse.testcakes.parkings.service.CarService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingExitServiceImpl implements ParkingExitService {

    private final ParkingTransactionRepository transactionRepo;
    private final ParkingSpotService spotService;
    private final CarService carService;

    @Override
    @Transactional
    public ParkingTransaction processExit(String licensePlate) {
        ParkingTransaction transaction = transactionRepo
            .findFirstByCarLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(licensePlate)
            .orElseThrow(() -> new CarNotFoundException(String.format(
                    "Car with %s license plate in not parked", licensePlate)));

        transaction.setExitTime(LocalDateTime.now());
        spotService.releaseSpot(transaction.getSpot().getId());
        carService.updateCarExitTime(licensePlate, LocalDateTime.now());

        
        return transactionRepo.save(transaction);
    }
}