package com.gitverse.testcakes.parkings.util;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.ParkingSpotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkingSpotInitializer {

    private final ParkingSpotRepository spotRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (spotRepository.count() == 0) {
            List<ParkingSpot> spots = new ArrayList<>();
            
            for (int i = 1; i <= 20; i++) {
                spots.add(createSpot(CarType.PASSENGER));
            }
            
            for (int i = 1; i <= 10; i++) {
                spots.add(createSpot(CarType.TRUCK));
            }

            for (int i = 1; i <= 10; i++) {
                spots.add(createSpot(CarType.MOTORCYCLE));
            }

            for (int i = 1; i <= 10; i++) {
                spots.add(createSpot(CarType.SPECIAL));
            }
            
            spotRepository.saveAll(spots);
            log.info("Initialized {} parking spots", spots.size());
        }
    }

    private ParkingSpot createSpot(CarType type) {
        return ParkingSpot.builder()
                .spotType(type)
                .occupied(false)
                .build();
    }

}