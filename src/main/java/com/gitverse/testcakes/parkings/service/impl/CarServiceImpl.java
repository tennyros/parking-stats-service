package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.CarRepository;
import com.gitverse.testcakes.parkings.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    @Transactional
    public Car findOrCreateCar(String licensePlate, CarType type) {
        Car car = carRepository.findByLicensePlateWithTransactions(licensePlate);
        if (car != null) {
            return car;
        }
        
        car = new Car();
        car.setLicensePlate(licensePlate);
        car.setType(type);
        car.setEntryTime(LocalDateTime.now());
        return carRepository.save(car);
    }

    @Override
    @Transactional
    public void updateCarExitTime(String licensePlate, LocalDateTime exitTime) {
        Car car = carRepository.findByLicensePlateWithTransactions(licensePlate);
        if (car != null) {
            car.setExitTime(exitTime);
            carRepository.save(car);
        }
    }
}
