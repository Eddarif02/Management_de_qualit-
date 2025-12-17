package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO pour la création/mise à jour d'un produit (entrée API)
 */
public record ProductRequestDTO(
        @NotBlank(message = "Le nom du produit est requis") String name,

        @NotNull(message = "Le prix est requis") @Positive(message = "Le prix doit être positif") Double price,

        @NotNull(message = "Le stock est requis") @PositiveOrZero(message = "Le stock doit être positif ou zéro") Integer stock) {
}
