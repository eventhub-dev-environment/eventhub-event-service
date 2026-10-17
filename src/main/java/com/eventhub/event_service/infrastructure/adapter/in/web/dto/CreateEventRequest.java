package com.eventhub.event_service.infrastructure.adapter.in.web.dto;

import com.eventhub.event_service.domain.model.EventBranding;
import com.eventhub.event_service.domain.model.EventSession;
import com.eventhub.event_service.domain.model.EventStatus;
import com.eventhub.event_service.domain.model.RegistrationField;
import com.eventhub.event_service.domain.model.TicketTier;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateEventRequest {

    @NotBlank(message = "El título del evento es obligatorio")
    private String title;

    private String description;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoryId;

    @Future(message = "La fecha del evento debe ser posterior a la fecha actual")
    private LocalDateTime eventDate;

    @NotBlank(message = "La ubicación es obligatoria")
    private String location;

    private EventStatus status;

    @Valid
    private List<TicketTier> ticketTiers;

    @Size(max = 50, message = "El tipo de evento no puede superar 50 caracteres")
    private String eventType;

    @Valid
    private List<EventSession> sessions;

    @Valid
    private List<RegistrationField> registrationFields;

    private EventBranding branding;
}