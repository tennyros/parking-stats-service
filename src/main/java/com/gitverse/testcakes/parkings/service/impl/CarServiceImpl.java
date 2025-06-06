package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.CarRepository;
import com.gitverse.testcakes.parkings.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of the CarService interface.
 *
 * @see CarService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    @Transactional
    public Car findOrRegisterCar(String licensePlate, CarType type) {
        return carRepository.findByLicensePlateWithTransactions(licensePlate)
                .orElseGet(() -> {
                    log.debug("Registering new car with license plate {} and type {}", licensePlate, type);
                    Car car = Car.builder()
                            .licensePlate(licensePlate)
                            .type(type)
                            .entryTime(LocalDateTime.now())
                            .build();
                    return carRepository.save(car);
                });
    }

    @Override
    @Transactional
    public void updateCarExitTime(String licensePlate, LocalDateTime exitTime) {
        carRepository.findByLicensePlateWithTransactions(licensePlate)
                .ifPresent(car -> {
                    car.setExitTime(exitTime);
                    carRepository.save(car);
                    log.debug("Updated exit time for car {} to {}", licensePlate, exitTime);
                });
    }
}
