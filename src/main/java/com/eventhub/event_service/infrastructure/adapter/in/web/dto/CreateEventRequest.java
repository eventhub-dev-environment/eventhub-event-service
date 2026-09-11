package com.eventhub.event_service.infrastructure.adapter.in.web.dto;

import com.eventhub.event_service.domain.model.EventStatus;
import com.eventhub.event_service.domain.model.TicketTier;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateEventRequest {

    @NotBlank(message = "El tenantId es obligatorio")
    private String tenantId;

    @NotBlank(message = "El título del evento es obligatorio")
    private String title;

    private String description;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoryId;

    @NotNull(message = "La fecha del evento es obligatoria")
    @Future(message = "La fecha del evento debe ser posterior a la fecha actual")
    private LocalDateTime eventDate;

    @NotBlank(message = "La ubicación es obligatoria")
    private String location;

    private EventStatus status;

    @NotEmpty(message = "Debe definir al menos un tipo de boletería / tarifa")
    private List<TicketTier> ticketTiers;
}