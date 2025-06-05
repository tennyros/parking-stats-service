package com.gitverse.testcakes.parkings.exception;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(String message) {
        super(message);
    }

}
