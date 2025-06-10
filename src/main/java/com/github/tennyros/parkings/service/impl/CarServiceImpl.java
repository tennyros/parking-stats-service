package com.github.tennyros.parkings.service.impl;

import com.github.tennyros.parkings.entity.Car;
import com.github.tennyros.parkings.entity.enums.CarType;
import com.github.tennyros.parkings.repository.CarRepository;
import com.github.tennyros.parkings.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return carRepository.findById(licensePlate)
                .orElseGet(() -> {
                    log.debug("Registering new car with license plate {} and type {}", licensePlate, type);
                    Car car = Car.builder()
                            .licensePlate(licensePlate)
                            .type(type)
                            .build();
                    return carRepository.save(car);
                });
    }
}
