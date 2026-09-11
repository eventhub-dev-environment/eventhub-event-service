package com.eventhub.event_service.domain.port.in;

import com.eventhub.event_service.domain.model.Event;
import java.util.List;
import java.util.Optional;

public interface GetEventUseCase {
    Optional<Event> getById(String id);
    List<Event> getAllByTenant(String tenantId);
    List<Event> getAllPublished();
}