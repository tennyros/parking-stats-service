package com.gitverse.testcakes.parkings.exception;

public class NoAvailableSpotsException extends RuntimeException {

    public NoAvailableSpotsException(String message) {
        super(message);
    }

    public NoAvailableSpotsException(String message, Throwable cause) {
        super(message, cause);
    }

}
