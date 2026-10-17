package com.eventhub.event_service.infrastructure.adapter.out.persistence.entity;

import com.eventhub.event_service.domain.model.EventBranding;
import com.eventhub.event_service.domain.model.EventSession;
import com.eventhub.event_service.domain.model.EventStatus;
import com.eventhub.event_service.domain.model.RegistrationField;
import com.eventhub.event_service.domain.model.TicketTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class EventDocument {
    @Id
    private String id;
    private String tenantId;
    private String eventType;
    private String title;
    private String description;
    private String categoryId;
    private LocalDateTime eventDate;
    private String location;
    private EventStatus status;
    private List<TicketTier> ticketTiers;
    private List<EventSession> sessions;
    private List<RegistrationField> registrationFields;
    private EventBranding branding; // <--- Se mapea automáticamente como subdocumento Mongo
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}