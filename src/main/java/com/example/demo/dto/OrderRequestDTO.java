package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO pour la création d'une commande (entrée API)
 */
public record OrderRequestDTO(
        @NotNull(message = "L'ID du produit est requis") Long productId,

        @NotNull(message = "La quantité est requise") @Positive(message = "La quantité doit être positive") Integer quantity,

        @Email(message = "L'email doit être valide") String customerEmail,

        String customerPhone) {
}
