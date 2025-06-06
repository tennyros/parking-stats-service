package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.repository.CarRepository;
import com.gitverse.testcakes.parkings.repository.ParkingTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.gitverse.testcakes.parkings.util.TestData.TEST_CAR_TYPE;
import static com.gitverse.testcakes.parkings.util.TestData.TEST_EXIT_TIME;
import static com.gitverse.testcakes.parkings.util.TestData.TEST_LICENSE_PLATE;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestCar;
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
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(Optional.of(testCar));

        Car result = carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE);

        assertNotNull(result);
        assertEquals(TEST_LICENSE_PLATE, result.getLicensePlate());
        assertEquals(TEST_CAR_TYPE, result.getType());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void findOrRegisterCar_WhenCarDoesNotExist_CreatesNewCar() {
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(Optional.empty());
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Car result = carService.findOrRegisterCar(TEST_LICENSE_PLATE, TEST_CAR_TYPE);

        assertNotNull(result);
        assertEquals(TEST_LICENSE_PLATE, result.getLicensePlate());
        assertEquals(TEST_CAR_TYPE, result.getType());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void updateCarExitTime_WhenCarExists_UpdatesExitTime() {
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        carService.updateCarExitTime(TEST_LICENSE_PLATE, TEST_EXIT_TIME);

        verify(carRepository).save(any(Car.class));
    }

    @Test
    void updateCarExitTime_WhenCarDoesNotExist_DoesNothing() {
        when(carRepository.findByLicensePlateWithTransactions(TEST_LICENSE_PLATE))
                .thenReturn(Optional.empty());

        carService.updateCarExitTime(TEST_LICENSE_PLATE, TEST_EXIT_TIME);

        verify(carRepository, never()).save(any(Car.class));
    }
} 