package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.repository.ParkingSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.gitverse.testcakes.parkings.util.TestData.TEST_CAR_TYPE;
import static com.gitverse.testcakes.parkings.util.TestData.TEST_ID;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestCar;
import static com.gitverse.testcakes.parkings.util.TestData.buildTestSpot;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingSpotServiceImplTest {

    @Mock
    private ParkingSpotRepository spotRepository;

    @InjectMocks
    private ParkingSpotServiceImpl spotService;

    private Car testCar;
    private ParkingSpot testSpot;

    @BeforeEach
    void setUp() {
        testCar = buildTestCar();
        testSpot = buildTestSpot();
    }

    @Test
    void occupySpot_WhenSpotExists_UpdatesSpot() {
        when(spotRepository.findFirstBySpotTypeAndOccupiedFalse(TEST_CAR_TYPE))
                .thenReturn(Optional.of(testSpot));
        when(spotRepository.save(any(ParkingSpot.class))).thenReturn(testSpot);

        ParkingSpot result = spotService.occupySpot(testCar);

        assertNotNull(result);
        assertTrue(result.isOccupied());
        verify(spotRepository).save(any(ParkingSpot.class));
    }

    @Test
    void occupySpot_WhenNoSpotExists_ThrowsException() {
        when(spotRepository.findFirstBySpotTypeAndOccupiedFalse(TEST_CAR_TYPE))
                .thenReturn(Optional.empty());

        assertThrows(NoAvailableSpotsException.class, () -> 
            spotService.occupySpot(testCar)
        );
    }

    @Test
    void freeSpot_WhenSpotExists_UpdatesSpot() {
        ParkingSpot occupiedSpot = buildTestSpot(TEST_ID, TEST_CAR_TYPE, true);
        when(spotRepository.findById(TEST_ID))
                .thenReturn(Optional.of(occupiedSpot));
        when(spotRepository.save(any(ParkingSpot.class))).thenReturn(occupiedSpot);

        spotService.freeSpot(TEST_ID);

        assertFalse(occupiedSpot.isOccupied());
        verify(spotRepository).save(any(ParkingSpot.class));
    }

    @Test
    void freeSpot_WhenSpotDoesNotExist_ThrowsException() {
        when(spotRepository.findById(TEST_ID))
                .thenReturn(Optional.empty());

        assertThrows(NoAvailableSpotsException.class, () -> 
            spotService.freeSpot(TEST_ID)
        );
    }
} 