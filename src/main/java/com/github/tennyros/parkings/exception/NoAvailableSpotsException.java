package com.github.tennyros.parkings.exception;

public class NoAvailableSpotsException extends RuntimeException {

    public NoAvailableSpotsException(String message) {
        super(message);
    }
}
