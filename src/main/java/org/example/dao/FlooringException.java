package org.example.dao;

public class FlooringException extends RuntimeException {

    //displaying exceptions
    public FlooringException(String message) {
        super(message);
    }

    public FlooringException(String message, Throwable cause) {
        super(message, cause);
    }
}
