package com.eventhub.event_service.application.usecase;

import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.model.EventStatus;
import com.eventhub.event_service.domain.port.in.CreateEventUseCase;
import com.eventhub.event_service.domain.port.in.GetEventUseCase;
import com.eventhub.event_service.domain.port.out.EventRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService implements CreateEventUseCase, GetEventUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    @Override
    public Event createEvent(Event event) {
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.DRAFT);
        }
        return eventRepositoryPort.save(event);
    }

    @Override
    public Optional<Event> getById(String id) {
        return eventRepositoryPort.findById(id);
    }

    @Override
    public List<Event> getAllByTenant(String tenantId) {
        return eventRepositoryPort.findByTenantId(tenantId);
    }

    @Override
    public List<Event> getAllPublished() {
        return eventRepositoryPort.findAllPublished();
    }
}