package com.gitverse.testcakes.parkings.exception;

public class CarAlreadyParkedException extends RuntimeException {

    public CarAlreadyParkedException(String licensePlate) {
        super(licensePlate);
    }
}
