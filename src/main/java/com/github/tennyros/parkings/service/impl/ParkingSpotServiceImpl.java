package com.github.tennyros.parkings.service.impl;

import com.github.tennyros.parkings.entity.Car;
import com.github.tennyros.parkings.entity.ParkingSpot;
import com.github.tennyros.parkings.entity.enums.CarType;
import com.github.tennyros.parkings.exception.NoAvailableSpotsException;
import com.github.tennyros.parkings.repository.ParkingSpotRepository;
import com.github.tennyros.parkings.repository.ParkingTransactionRepository;
import com.github.tennyros.parkings.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of the ParkingSpotService interface.
 *
 * @see ParkingSpotService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private final ParkingSpotRepository spotRepository;
    private final ParkingTransactionRepository transactionRepository;

    @Override
    @Transactional
    public ParkingSpot occupySpot(Car car) {
        CarType carType = car.getType();
        ParkingSpot spot = spotRepository.findFirstBySpotTypeAndOccupiedFalse(carType)
                .orElseThrow(() -> {
                    log.warn("No available spots found for car type {}", carType);
                    return new NoAvailableSpotsException(String.format(
                            "No available spots for %s type", carType));
                });
        spot.setCar(car);
        spot.setOccupied(true);
        spot.setLastOccupiedAt(LocalDateTime.now());
        log.debug("Occupied spot {} for car type {}", spot.getId(), carType);
        return spotRepository.save(spot);
    }

    @Override
    @Transactional
    public void freeSpot(Long spotId) {
        ParkingSpot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new NoAvailableSpotsException("Parking spot not found with ID: " + spotId));
        spot.setOccupied(false);
        spot.setLastFreedAt(LocalDateTime.now());
        spotRepository.save(spot);
        log.debug("Parking spot {} has been freed", spotId);
    }
}
