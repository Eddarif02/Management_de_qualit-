package com.example.demo.exception;

import java.time.LocalDateTime;

/**
 * Structure standardisée pour les réponses d'erreur API.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
}
