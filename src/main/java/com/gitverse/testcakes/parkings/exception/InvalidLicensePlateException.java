package com.gitverse.testcakes.parkings.exception;

public class InvalidLicensePlateException extends RuntimeException {

    public InvalidLicensePlateException(String message) {
        super(message);
    }

    public InvalidLicensePlateException(String message, Throwable cause) {
        super(message, cause);
    }

}
