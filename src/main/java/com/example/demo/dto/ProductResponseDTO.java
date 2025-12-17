package com.example.demo.dto;

/**
 * DTO pour la réponse produit (sortie API)
 * N'expose que les champs nécessaires au client
 */
public record ProductResponseDTO(
        Long id,
        String name,
        Double price,
        Integer stock) {
}
