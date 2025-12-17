package com.example.demo.mapper;

import com.example.demo.dto.OrderResponseDTO;
import com.example.demo.entity.Order;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre Order Entity et DTOs.
 */
@Component
public class OrderMapper {

    /**
     * Convertit une entité Order vers un OrderResponseDTO.
     * Les données sensibles (email, phone) ne sont pas exposées.
     */
    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }
        return new OrderResponseDTO(
                order.getId(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getStatus());
    }
}
