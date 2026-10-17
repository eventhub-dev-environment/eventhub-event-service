package com.eventhub.event_service.infrastructure.adapter.out.persistence;

import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.model.EventSession;
import com.eventhub.event_service.domain.model.InventoryMode;
import com.eventhub.event_service.domain.model.RegistrationField;
import com.eventhub.event_service.domain.model.SeatDefinition;
import com.eventhub.event_service.domain.model.TicketTier;
import com.eventhub.event_service.infrastructure.adapter.out.persistence.entity.EventDocument;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventMongoAdapterTest {

    @Test
    void persistsAndLoadsSessionsAndSeatDefinitions() {
        SpringDataMongoEventRepository repository = mock(SpringDataMongoEventRepository.class);
        EventMongoAdapter adapter = new EventMongoAdapter(repository);
        OffsetDateTime startsAt = OffsetDateTime.now().plusDays(2);
        Event event = Event.builder()
                .id("event-123")
                .tenantId("tenant-a")
                .eventType("CINEMA")
                .title("Evening show")
                .eventDate(startsAt.toLocalDateTime())
                .sessions(List.of(EventSession.builder()
                        .id("session-123")
                        .name("7 PM")
                        .startsAt(startsAt)
                        .endsAt(startsAt.plusHours(2))
                        .ticketTiers(List.of(TicketTier.builder()
                                .id("standard")
                                .name("Standard")
                                .price(new BigDecimal("12.50"))
                                .capacity(1)
                                .inventoryMode(InventoryMode.RESERVED_SEATING)
                                .seats(List.of(SeatDefinition.builder()
                                        .seatId("screen-1-a-1")
                                        .section("Screen 1")
                                        .row("A")
                                        .number("1")
                                        .build()))
                                .build()))
                        .build()))
                .registrationFields(List.of(RegistrationField.builder()
                        .key("shirtSize")
                        .label("T-shirt size")
                        .type("ENUM")
                        .required(true)
                        .build()))
                .build();
        when(repository.save(any(EventDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event saved = adapter.save(event);

        assertThat(saved.getEventType()).isEqualTo("CINEMA");
        assertThat(saved.getSessions()).hasSize(1);
        assertThat(saved.getSessions().getFirst().getTicketTiers().getFirst().getSeats().getFirst().getSeatId())
                .isEqualTo("screen-1-a-1");
        assertThat(saved.getRegistrationFields().getFirst().getKey()).isEqualTo("shirtSize");
    }

    @Test
    void readsLegacyEventsWithoutEventTypeAsOther() {
        SpringDataMongoEventRepository repository = mock(SpringDataMongoEventRepository.class);
        EventMongoAdapter adapter = new EventMongoAdapter(repository);
        when(repository.findById("legacy-event")).thenReturn(Optional.of(EventDocument.builder()
                .id("legacy-event")
                .ticketTiers(List.of(TicketTier.builder().id("general").build()))
                .build()));

        assertThat(adapter.findById("legacy-event"))
                .get()
                .extracting(Event::getEventType)
                .isEqualTo("OTHER");
        assertThat(adapter.findById("legacy-event"))
                .get()
                .extracting(event -> event.getTicketTiers().getFirst().getInventoryMode())
                .isEqualTo(InventoryMode.GENERAL_ADMISSION);
    }
}
