package com.eventhub.event_service.domain.port.in;

import com.eventhub.event_service.domain.model.Event;

public interface CreateEventUseCase {
    Event createEvent(Event event);
}