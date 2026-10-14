package com.eventhub.event_service.infrastructure.adapter.out.persistence;

import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.model.EventStatus;
import com.eventhub.event_service.domain.port.out.EventRepositoryPort;
import com.eventhub.event_service.infrastructure.adapter.out.persistence.entity.EventDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventMongoAdapter implements EventRepositoryPort {

    private final SpringDataMongoEventRepository repository;

    @Override
    public Event save(Event event) {
        EventDocument doc = mapToDocument(event);
        if (doc.getCreatedAt() == null) {
            doc.setCreatedAt(LocalDateTime.now());
        }
        doc.setUpdatedAt(LocalDateTime.now());

        EventDocument saved = repository.save(doc);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Event> findById(String id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Event> findByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Event> findAllPublished() {
        return repository.findByStatus(EventStatus.PUBLISHED).stream()
                .map(this::mapToDomain)
                .toList();
    }

    private EventDocument mapToDocument(Event domain) {
        return EventDocument.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .categoryId(domain.getCategoryId())
                .eventDate(domain.getEventDate())
                .location(domain.getLocation())
                .status(domain.getStatus())
                .ticketTiers(domain.getTicketTiers())
                .branding(domain.getBranding()) // Mapeo a Document
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Event mapToDomain(EventDocument doc) {
        return Event.builder()
                .id(doc.getId())
                .tenantId(doc.getTenantId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .categoryId(doc.getCategoryId())
                .eventDate(doc.getEventDate())
                .location(doc.getLocation())
                .status(doc.getStatus())
                .ticketTiers(doc.getTicketTiers())
                .branding(doc.getBranding()) // Mapeo a Domain
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}