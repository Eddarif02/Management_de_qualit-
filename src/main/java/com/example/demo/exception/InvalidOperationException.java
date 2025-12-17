package com.example.demo.exception;

/**
 * Exception levée quand une opération invalide est tentée.
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
