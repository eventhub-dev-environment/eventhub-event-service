package com.eventhub.event_service.domain.port.out;

import com.eventhub.event_service.domain.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
    Event save(Event event);
    Optional<Event> findById(String id);
    List<Event> findByTenantId(String tenantId);
    List<Event> findAllPublished();
}