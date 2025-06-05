package com.gitverse.testcakes.parkings.repository;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    Optional<ParkingSpot> findFirstBySpotTypeAndOccupiedFalse(CarType carType);

    boolean existsBySpotType(CarType carType);

}
