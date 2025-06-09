package com.github.tennyros.parkings.repository;

import com.github.tennyros.parkings.entity.ParkingSpot;
import com.github.tennyros.parkings.entity.enums.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    Optional<ParkingSpot> findFirstBySpotTypeAndOccupiedFalse(CarType carType);

    boolean existsBy();

}
