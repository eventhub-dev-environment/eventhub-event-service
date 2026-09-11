package com.eventhub.event_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTier {
    private String id;
    private String name;        // Ej: General, VIP, Preferencial
    private BigDecimal price;
    private Integer capacity;
    private Integer available;
}