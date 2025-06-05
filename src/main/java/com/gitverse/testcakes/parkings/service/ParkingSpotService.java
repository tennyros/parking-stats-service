package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;

public interface ParkingSpotService {

    ParkingSpot occupySpot(CarType carType);

    void releaseSpot(Long spotId);

}
