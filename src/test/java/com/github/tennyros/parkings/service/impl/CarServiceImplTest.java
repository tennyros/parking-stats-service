package com.github.tennyros.parkings.service.impl;

import com.github.tennyros.parkings.entity.Car;
import com.github.tennyros.parkings.repository.CarRepository;
import com.github.tennyros.parkings.repository.ParkingTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.github.tennyros.parkings.util.TestData.TEST_CAR_TYPE;
import static com.github.tennyros.parkings.util.TestData.TEST_LICENSE_PLATE;
import static com.github.tennyros.parkings.util.TestData.buildTestCar;
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

    @Mock
    private ParkingTransactionRepository transactionRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car testCar;

    @BeforeEach
    void setUp() {
        testCar = buildTestCar();
    }

    @Test
    void findOrRegisterCar_WhenCarExists_ReturnsExistingCar() {
        when(carRepository.findById(TEST_LICENSE_PLATE))
                .thenReturn(Optional.of(testCar));

        var result = carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE);

        assertNotNull(result);
        assertEquals(TEST_LICENSE_PLATE, result.getLicensePlate());
        assertEquals(TEST_CAR_TYPE, result.getType());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void findOrRegisterCar_WhenCarDoesNotExist_CreatesNewCar() {
        when(carRepository.findById(TEST_LICENSE_PLATE))
                .thenReturn(Optional.empty());
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        var result = carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE);

        assertNotNull(result);
        assertEquals(TEST_LICENSE_PLATE, result.getLicensePlate());
        assertEquals(TEST_CAR_TYPE, result.getType());
        verify(carRepository).save(any(Car.class));
    }
} 