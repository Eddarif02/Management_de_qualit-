package com.example.demo.exception;

/**
 * Exception levée quand une ressource n'est pas trouvée.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s avec l'ID %d non trouvé", resourceName, id));
    }
}
