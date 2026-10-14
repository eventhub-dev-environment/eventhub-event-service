package com.eventhub.event_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventBranding {
    private String logoUrl;
    private String primaryColor;   // Ej: "#FF5733"
    private String secondaryColor; // Ej: "#1A1A1A"
    private String supportEmail;   // Correo de soporte de la marca
    private String organizerName;  // Nombre público del organizador
    private String bannerUrl;
}