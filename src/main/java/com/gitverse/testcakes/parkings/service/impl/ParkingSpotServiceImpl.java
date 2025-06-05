package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.repository.ParkingSpotRepository;
import com.gitverse.testcakes.parkings.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private final ParkingSpotRepository spotRepository;

    @Override
    @Transactional
    public ParkingSpot occupySpot(CarType carType) {
        return spotRepository.findFirstBySpotTypeAndOccupiedFalse(carType)
                .map(spot -> {
                    spot.setOccupied(true);
                    return spotRepository.save(spot);
                })
                .orElseThrow(() -> new NoAvailableSpotsException("No free spot for type: " + carType));
    }

    @Override
    @Transactional
    public void releaseSpot(Long spotId) {
        spotRepository.findById(spotId).ifPresent(spot -> {
            spot.setOccupied(false);
            spot.setCar(null);
            spotRepository.save(spot);
        });
    }

}
