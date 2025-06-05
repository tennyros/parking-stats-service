package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.enums.CarType;

import java.time.LocalDateTime;

public interface CarService {

    Car findOrCreateCar(String licensePlate, CarType type);

    void updateCarExitTime(String licensePlate, LocalDateTime exitTime);

}
