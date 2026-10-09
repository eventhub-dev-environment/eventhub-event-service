package com.eventhub.event_service.infrastructure.adapter.in.web;

import com.eventhub.event_service.application.usecase.UpdateEventLogoUseCase; // <-- Importación agregada
import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.port.in.CreateEventUseCase;
import com.eventhub.event_service.domain.port.in.GetEventUseCase;
import com.eventhub.event_service.infrastructure.adapter.in.web.dto.ApiResponse;
import com.eventhub.event_service.infrastructure.adapter.in.web.dto.CreateEventRequest;
import com.eventhub.event_service.infrastructure.adapter.out.storage.S3StorageAdapter; // <-- Importación agregada
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final GetEventUseCase getEventUseCase;
    private final S3StorageAdapter s3StorageAdapter; // <-- Atributo inyectado
    private final UpdateEventLogoUseCase updateEventLogoUseCase; // <-- Atributo inyectado

    @PostMapping
    public ResponseEntity<ApiResponse<Event>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event event = Event.builder()
                .tenantId(request.getTenantId())
                .title(request.getTitle())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .eventDate(request.getEventDate())
                .location(request.getLocation())
                .status(request.getStatus())
                .ticketTiers(request.getTicketTiers())
                .branding(request.getBranding())
                .build();

        Event created = createEventUseCase.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Evento creado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getById(@PathVariable String id) {
        return getEventUseCase.getById(id)
                .map(event -> ResponseEntity.ok(ApiResponse.success(event, "Evento encontrado")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Evento no encontrado con ID: " + id, null)));
    }

    @GetMapping("/published")
    public ResponseEntity<ApiResponse<List<Event>>> getPublishedEvents() {
        List<Event> events = getEventUseCase.getAllPublished();
        return ResponseEntity.ok(ApiResponse.success(events, "Eventos publicados recuperados exitosamente"));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<Event>>> getEventsByTenant(@PathVariable String tenantId) {
        List<Event> events = getEventUseCase.getAllByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(events, "Eventos del tenant recuperados exitosamente"));
    }

    @PostMapping(value = "/{id}/branding/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Event>> uploadEventLogo(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) {
        // 1. Almacenar la imagen en MinIO en la carpeta 'logos'
        String logoUrl = s3StorageAdapter.uploadFile(file, "logos");

        // 2. Asociar la URL pública generada a la entidad del Evento
        Event updatedEvent = updateEventLogoUseCase.execute(id, logoUrl);

        return ResponseEntity.ok(ApiResponse.success(updatedEvent, "Logo del evento actualizado exitosamente"));
    }
}