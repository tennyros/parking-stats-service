package com.gitverse.testcakes.parkings.service.impl;

import com.gitverse.testcakes.parkings.entity.ParkingSpot;
import com.gitverse.testcakes.parkings.entity.enums.CarType;
import com.gitverse.testcakes.parkings.exception.NoAvailableSpotsException;
import com.gitverse.testcakes.parkings.repository.ParkingSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingSpotServiceImplTest {

    @Mock
    private ParkingSpotRepository spotRepository;

    @InjectMocks
    private ParkingSpotServiceImpl spotService;

    private ParkingSpot testSpot;

    @BeforeEach
    void setUp() {
        testSpot = new ParkingSpot();
        testSpot.setId(1L);
        testSpot.setSpotType(CarType.PASSENGER);
        testSpot.setOccupied(false);
    }

    @Test
    void occupySpot_Success() {
        when(spotRepository.findFirstBySpotTypeAndOccupiedFalse(CarType.PASSENGER))
                .thenReturn(Optional.of(testSpot));
        when(spotRepository.save(any(ParkingSpot.class))).thenReturn(testSpot);

        ParkingSpot result = spotService.occupySpot(CarType.PASSENGER);

        assertNotNull(result);
        assertTrue(result.isOccupied());
        verify(spotRepository).findFirstBySpotTypeAndOccupiedFalse(CarType.PASSENGER);
        verify(spotRepository).save(testSpot);
    }

    @Test
    void occupySpot_NoAvailableSpots() {
        when(spotRepository.findFirstBySpotTypeAndOccupiedFalse(CarType.PASSENGER))
                .thenReturn(Optional.empty());

        assertThrows(NoAvailableSpotsException.class, () -> spotService.occupySpot(CarType.PASSENGER));
        verify(spotRepository).findFirstBySpotTypeAndOccupiedFalse(CarType.PASSENGER);
        verify(spotRepository, never()).save(any());
    }

    @Test
    void releaseSpot_Success() {
        testSpot.setOccupied(true);
        when(spotRepository.findById(1L)).thenReturn(Optional.of(testSpot));
        when(spotRepository.save(any(ParkingSpot.class))).thenReturn(testSpot);

        spotService.releaseSpot(1L);

        verify(spotRepository).findById(1L);
        verify(spotRepository).save(testSpot);
        assertFalse(testSpot.isOccupied());
        assertNull(testSpot.getCar());
    }

    @Test
    void releaseSpot_SpotNotFound() {
        when(spotRepository.findById(1L)).thenReturn(Optional.empty());

        spotService.releaseSpot(1L);

        verify(spotRepository).findById(1L);
        verify(spotRepository, never()).save(any());
    }
} 