package com.example.demo.service;

import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.dto.OrderResponseDTO;

import java.util.List;

/**
 * Interface du service Order.
 * Définit le contrat pour les opérations métier sur les commandes.
 */
public interface OrderService {

    List<OrderResponseDTO> findAll();

    OrderResponseDTO findById(Long id);

    List<OrderResponseDTO> findByProductId(Long productId);

    List<OrderResponseDTO> findByStatus(String status);

    OrderResponseDTO create(OrderRequestDTO orderRequest);

    OrderResponseDTO cancel(Long id);

    void delete(Long id);
}
