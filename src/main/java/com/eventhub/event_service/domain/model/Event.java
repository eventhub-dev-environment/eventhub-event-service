package com.eventhub.event_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String id;
    private String tenantId;    // Identificador del organizador / empresa
    private String eventType;
    private String title;
    private String description;
    private String categoryId;
    private LocalDateTime eventDate;
    private String location;    // Ciudad / Dirección o recinto
    private EventStatus status;
    private List<TicketTier> ticketTiers;
    private List<EventSession> sessions;
    private List<RegistrationField> registrationFields;
    private EventBranding branding; // <--- Nuevo campo para Multi-Tenant Branding
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}