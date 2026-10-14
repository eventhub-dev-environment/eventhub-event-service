package com.eventhub.event_service.application.usecase;

import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.model.EventBranding;
import com.eventhub.event_service.domain.port.out.EventRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateEventLogoUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    public Event execute(String eventId, String logoUrl) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + eventId));

        if (event.getBranding() == null) {
            event.setBranding(new EventBranding());
        }

        event.getBranding().setLogoUrl(logoUrl);
        return eventRepositoryPort.save(event);
    }
}