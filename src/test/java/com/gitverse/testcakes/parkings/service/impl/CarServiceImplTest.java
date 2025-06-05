package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private static final String TEST_LICENSE_PLATE = "А123ВС777";
    private static final CarType TEST_CAR_TYPE = CarType.PASSENGER;

    private Car testCar;

    @BeforeEach
    void setUp() {
        testCar = new Car();
        testCar.setLicensePlate(TEST_LICENSE_PLATE);
        testCar.setType(TEST_CAR_TYPE);
        testCar.setEntryTime(LocalDateTime.now());
    }

    @Test
    void findOrCreateCar_WhenCarExists_ReturnsExistingCar() {
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(testCar);

        Car result = carService.findOrCreateCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE);

        assertNotNull(result);
        assertEquals(TEST_LICENSE_PLATE, result.getLicensePlate());
        assertEquals(TEST_CAR_TYPE, result.getType());
        verify(carRepository).findByLicensePlateWithTransactions(TEST_LICENSE_PLATE);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void findOrCreateCar_WhenCarDoesNotExist_CreatesNewCar() {
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(null);
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            car.setEntryTime(LocalDateTime.now());
            return car;
        });

        Car result = carService.findOrCreateCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE);

        assertNotNull(result);
        assertEquals(TEST_LICENSE_PLATE, result.getLicensePlate());
        assertEquals(TEST_CAR_TYPE, result.getType());
        assertNotNull(result.getEntryTime());
        verify(carRepository).findByLicensePlateWithTransactions(TEST_LICENSE_PLATE);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void updateCarExitTime_WhenCarExists_UpdatesExitTime() {
        LocalDateTime exitTime = LocalDateTime.now();
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(testCar);
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        carService.updateCarExitTime(TEST_LICENSE_PLATE, exitTime);

        assertEquals(exitTime, testCar.getExitTime());
        verify(carRepository).findByLicensePlateWithTransactions(TEST_LICENSE_PLATE);
        verify(carRepository).save(testCar);
    }

    @Test
    void updateCarExitTime_WhenCarDoesNotExist_DoesNothing() {
        LocalDateTime exitTime = LocalDateTime.now();
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(null);

        carService.updateCarExitTime(TEST_LICENSE_PLATE, exitTime);

        verify(carRepository).findByLicensePlateWithTransactions(TEST_LICENSE_PLATE);
        verify(carRepository, never()).save(any(Car.class));
    }
} 