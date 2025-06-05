package com.gitverse.testcakes.parkings.util;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.ParkingSpotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
                spots.add(
                    ParkingSpot.builder()
                        .spotType(CarType.PASSENGER)
                        .occupied(false)
                        .build()
                );
            }
            
            for (int i = 1; i <= 10; i++) {
                spots.add(
                    ParkingSpot.builder()
                        .spotType(CarType.TRUCK)
                        .occupied(false)
                        .build()
                );
            }

            for (int i = 1; i <= 10; i++) {
                spots.add(
                        ParkingSpot.builder()
                                .spotType(CarType.MOTORCYCLE)
                                .occupied(false)
                                .build()
                );
            }

            for (int i = 1; i <= 10; i++) {
                spots.add(
                        ParkingSpot.builder()
                                .spotType(CarType.SPECIAL)
                                .occupied(false)
                                .build()
                );
            }
            
            spotRepository.saveAll(spots);
        }
    }
}