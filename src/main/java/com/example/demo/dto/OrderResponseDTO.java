package com.example.demo.dto;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse commande (sortie API)
 * N'expose pas les données sensibles du client
 */
public record OrderResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        Double unitPrice,
        Double totalPrice,
        LocalDateTime orderDate,
        String status) {
}
