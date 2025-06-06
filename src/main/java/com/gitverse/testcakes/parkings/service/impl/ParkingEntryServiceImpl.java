package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import com.gitverse.testcakes.parkings.exception.CarAlreadyParkedException;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import com.gitverse.testcakes.parkings.service.CarService;
import com.gitverse.testcakes.parkings.service.ParkingEntryService;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of the ParkingEntryService interface.
 *
 * @see ParkingEntryService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingEntryServiceImpl implements ParkingEntryService {

    private final CarService carService;
    private final ParkingSpotService spotService;
    private final ParkingTransactionRepository transactionRepo;

    @Override
    @Transactional
    public ParkingTransaction registerEntry(CarEntryRequest request) {
        String normalizedPlate = normalizeLicensePlate(request.licensePlate());
        Car car = carService.findOrRegisterCar(normalizedPlate, request.type());
        checkIfCarAlreadyParked(car);
        ParkingSpot spot = spotService.occupySpot(car.getType());
        ParkingTransaction transaction = buildTransaction(car, spot);
        log.debug("Car {} parked at spot {}", normalizedPlate, spot.getId());

        return transaction;
    }

    private void checkIfCarAlreadyParked(Car car) {
        if (transactionRepo.existsByCarAndExitTimeIsNull(car)) {
            throw new CarAlreadyParkedException(String.format(
                    "Car with %s license plate already parked", car.getLicensePlate()));
        }
    }

    private ParkingTransaction buildTransaction(Car car, ParkingSpot spot) {
        return transactionRepo.save(
                ParkingTransaction.builder()
                        .car(car)
                        .spot(spot)
                        .entryTime(LocalDateTime.now())
                        .build()
        );
    }

    private String normalizeLicensePlate(String plate) {
        return plate.replaceAll("[\\s-]", "").toUpperCase();
    }
}
