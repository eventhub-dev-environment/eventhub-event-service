package com.eventhub.event_service.infrastructure.adapter.in.web;

import com.eventhub.event_service.application.usecase.UpdateEventLogoUseCase;
import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.port.in.CreateEventUseCase;
import com.eventhub.event_service.domain.port.in.GetEventUseCase;
import com.eventhub.event_service.infrastructure.adapter.in.web.dto.ApiResponse;
import com.eventhub.event_service.infrastructure.adapter.in.web.dto.CreateEventRequest;
import com.eventhub.event_service.infrastructure.adapter.out.storage.S3StorageAdapter;
import com.eventhub.event_service.application.usecase.UpdateEventBannerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final GetEventUseCase getEventUseCase;
    private final S3StorageAdapter s3StorageAdapter;
    private final UpdateEventLogoUseCase updateEventLogoUseCase;
    private final UpdateEventBannerUseCase updateEventBannerUseCase;
    private final EventAccessPolicy accessPolicy;

    @PostMapping
    public ResponseEntity<ApiResponse<Event>> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-Tenant-Roles") String tenantRoles,
            @RequestHeader("X-User-Roles") String userRoles) {
        accessPolicy.requireTenantStaff(tenantId, tenantId, tenantRoles, userRoles);
        Event event = Event.builder()
                .tenantId(tenantId)
                .eventType(request.getEventType())
                .title(request.getTitle())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .eventDate(request.getEventDate())
                .location(request.getLocation())
                .status(request.getStatus())
                .ticketTiers(request.getTicketTiers())
                .sessions(request.getSessions())
                .registrationFields(request.getRegistrationFields())
                .branding(request.getBranding())
                .build();

        Event created = createEventUseCase.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Evento creado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getById(
            @PathVariable String id,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            @RequestHeader(value = "X-Tenant-Roles", required = false) String tenantRoles,
            @RequestHeader(value = "X-User-Roles", required = false) String userRoles) {
        return getEventUseCase.getById(id)
                .map(event -> {
                    if (event.getStatus() != com.eventhub.event_service.domain.model.EventStatus.PUBLISHED) {
                        accessPolicy.requireTenantStaff(event.getTenantId(), tenantId, tenantRoles, userRoles);
                    }
                    return ResponseEntity.ok(ApiResponse.success(event, "Evento encontrado"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Evento no encontrado con ID: " + id, null)));
    }

    @GetMapping("/published")
    public ResponseEntity<ApiResponse<List<Event>>> getPublishedEvents() {
        List<Event> events = getEventUseCase.getAllPublished();
        return ResponseEntity.ok(ApiResponse.success(events, "Eventos publicados recuperados exitosamente"));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<Event>>> getEventsByTenant(
            @PathVariable String tenantId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String activeTenantId,
            @RequestHeader(value = "X-Tenant-Roles", required = false) String tenantRoles,
            @RequestHeader("X-User-Roles") String userRoles) {
        accessPolicy.requireTenantStaff(tenantId, activeTenantId, tenantRoles, userRoles);
        List<Event> events = getEventUseCase.getAllByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(events, "Eventos del tenant recuperados exitosamente"));
    }

    @PostMapping(value = "/{id}/branding/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Event>> uploadEventLogo(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            @RequestHeader(value = "X-Tenant-Roles", required = false) String tenantRoles,
            @RequestHeader("X-User-Roles") String userRoles
    ) {
        Event existingEvent = getEventUseCase.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        accessPolicy.requireTenantStaff(existingEvent.getTenantId(), tenantId, tenantRoles, userRoles);
        String logoUrl = s3StorageAdapter.uploadFile(file, "logos");
        Event updatedEvent = updateEventLogoUseCase.execute(id, logoUrl);

        return ResponseEntity.ok(ApiResponse.success(updatedEvent, "Logo del evento actualizado exitosamente"));
    }

    @PostMapping(value = "/{id}/branding/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Event>> uploadEventBanner(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            @RequestHeader(value = "X-Tenant-Roles", required = false) String tenantRoles,
            @RequestHeader("X-User-Roles") String userRoles
    ) {
        Event existingEvent = getEventUseCase.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        accessPolicy.requireTenantStaff(existingEvent.getTenantId(), tenantId, tenantRoles, userRoles);
        String bannerUrl = s3StorageAdapter.uploadFile(file, "banners");
        Event updatedEvent = updateEventBannerUseCase.execute(id, bannerUrl);

        return ResponseEntity.ok(ApiResponse.success(updatedEvent, "Banner del evento actualizado exitosamente"));
    }
}