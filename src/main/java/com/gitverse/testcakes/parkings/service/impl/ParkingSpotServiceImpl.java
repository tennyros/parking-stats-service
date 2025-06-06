package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.repository.ParkingSpotRepository;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public ParkingSpot occupySpot(CarType carType) {
        ParkingSpot spot = spotRepository.findFirstBySpotTypeAndOccupiedFalse(carType)
                .orElseThrow(() -> {
                    log.warn("No available spots found for car type {}", carType);
                    return new NoAvailableSpotsException(String.format(
                            "No available spots for %s type", carType));
                });
        
        spot.setOccupied(true);
        log.debug("Occupied spot {} for car type {}", spot.getId(), carType);
        return spotRepository.save(spot);
    }

    @Override
    @Transactional
    public void freeSpot(Long spotId) {
        ParkingSpot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new NoAvailableSpotsException("Parking spot not found with ID: " + spotId));
        
        spot.setOccupied(false);
        spotRepository.save(spot);
        log.debug("Parking spot {} has been freed", spotId);
    }
}
