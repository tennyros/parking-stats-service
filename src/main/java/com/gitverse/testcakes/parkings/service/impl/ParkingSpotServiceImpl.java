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

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private final ParkingSpotRepository spotRepository;

    @Override
    @Transactional
    public ParkingSpot occupySpot(CarType type) {
        ParkingSpot spot = spotRepository.findFirstBySpotTypeAndOccupiedFalse(type)
                .orElseThrow(() -> {
                    log.warn("No available spots found for car type {}", type);
                    return new NoAvailableSpotsException(String.format(
                            "No available spots for %s type", type));
                });

        spot.setOccupied(true);
        log.debug("Occupied spot {} for car type {}", spot.getId(), type);
        return spotRepository.save(spot);
    }

    @Override
    @Transactional
    public void releaseSpot(Long spotId) {
        spotRepository.findById(spotId).ifPresent(spot -> {
            spot.setOccupied(false);
            spotRepository.save(spot);
            log.debug("Released spot {}", spotId);
        });
    }

}
