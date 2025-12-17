package com.example.demo.exception;

/**
 * Exception levée quand le stock est insuffisant pour une commande.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Stock insuffisant pour '%s'. Demandé: %d, Disponible: %d",
                productName, requested, available));
    }
}
