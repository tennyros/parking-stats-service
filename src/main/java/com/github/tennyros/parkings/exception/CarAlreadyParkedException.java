package com.github.tennyros.parkings.exception;

public class CarAlreadyParkedException extends RuntimeException {

    public CarAlreadyParkedException(String licensePlate) {
        super(licensePlate);
    }
}
