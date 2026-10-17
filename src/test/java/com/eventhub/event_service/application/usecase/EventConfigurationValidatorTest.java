package com.eventhub.event_service.application.usecase;

import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.model.EventSession;
import com.eventhub.event_service.domain.model.InventoryMode;
import com.eventhub.event_service.domain.exception.InvalidEventConfigurationException;
import com.eventhub.event_service.domain.model.RegistrationField;
import com.eventhub.event_service.domain.model.RegistrationFieldOption;
import com.eventhub.event_service.domain.model.SeatDefinition;
import com.eventhub.event_service.domain.model.TicketTier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventConfigurationValidatorTest {

    private final EventConfigurationValidator validator = new EventConfigurationValidator();

    @Test
    void defaultsLegacyTierToGeneralAdmission() {
        Event event = Event.builder()
                .eventDate(OffsetDateTime.now().plusDays(1).toLocalDateTime())
                .ticketTiers(List.of(tier("general", 100)))
                .build();

        validator.normalizeAndValidate(event);

        assertThat(event.getEventType()).isEqualTo("OTHER");
        assertThat(event.getTicketTiers().getFirst().getInventoryMode())
                .isEqualTo(InventoryMode.GENERAL_ADMISSION);
    }

    @Test
    void derivesEventDateAndSeatCapacityForMultiSessionEvents() {
        OffsetDateTime startsAt = OffsetDateTime.now().plusDays(2);
        Event event = Event.builder()
                .sessions(List.of(EventSession.builder()
                        .name("Saturday matinee")
                        .startsAt(startsAt)
                        .endsAt(startsAt.plusHours(2))
                        .ticketTiers(List.of(TicketTier.builder()
                                .id("balcony")
                                .name("Balcony")
                                .price(new BigDecimal("25.00"))
                                .inventoryMode(InventoryMode.RESERVED_SEATING)
                                .seats(List.of(seat("A-1", "A", "1")))
                                .build()))
                        .build()))
                .build();

        validator.normalizeAndValidate(event);

        EventSession session = event.getSessions().getFirst();
        assertThat(session.getId()).isNotBlank();
        assertThat(event.getEventDate()).isEqualTo(startsAt.toLocalDateTime());
        assertThat(session.getTicketTiers().getFirst().getCapacity()).isEqualTo(1);
    }

    @Test
    void rejectsTheSameSeatInDifferentTiersOfOneSession() {
        OffsetDateTime startsAt = OffsetDateTime.now().plusDays(2);
        EventSession session = EventSession.builder()
                .name("Main show")
                .startsAt(startsAt)
                .endsAt(startsAt.plusHours(2))
                .ticketTiers(List.of(
                        reservedTier("standard", "seat-1"),
                        reservedTier("premium", "seat-1")))
                .build();

        assertThatThrownBy(() -> validator.normalizeAndValidate(Event.builder()
                        .sessions(List.of(session))
                        .build()))
                .isInstanceOf(InvalidEventConfigurationException.class);
    }

    @Test
    void requiresSessionsOrSingleOccurrenceTiersButNotBoth() {
        OffsetDateTime startsAt = OffsetDateTime.now().plusDays(2);
        Event event = Event.builder()
                .eventDate(startsAt.toLocalDateTime())
                .ticketTiers(List.of(tier("general", 20)))
                .sessions(List.of(EventSession.builder()
                        .name("Session")
                        .startsAt(startsAt)
                        .endsAt(startsAt.plusHours(1))
                        .ticketTiers(List.of(tier("session-tier", 20)))
                        .build()))
                .build();

        assertThatThrownBy(() -> validator.normalizeAndValidate(event))
                .isInstanceOf(InvalidEventConfigurationException.class);
    }

    @Test
    void enumRegistrationFieldsRequireUniqueNonEmptyOptions() {
        RegistrationField field = RegistrationField.builder()
                .key("shirtSize")
                .label("T-shirt size")
                .type("ENUM")
                .required(true)
                .options(List.of(
                        RegistrationFieldOption.builder().label("Small").value("S").build(),
                        RegistrationFieldOption.builder().label("Large").value("L").build()))
                .build();
        Event event = Event.builder()
                .eventDate(OffsetDateTime.now().plusDays(1).toLocalDateTime())
                .ticketTiers(List.of(tier("general", 20)))
                .registrationFields(List.of(field))
                .build();

        assertThatCode(() -> validator.normalizeAndValidate(event)).doesNotThrowAnyException();
    }

    private TicketTier tier(String id, int capacity) {
        return TicketTier.builder()
                .id(id)
                .name(id)
                .price(new BigDecimal("10.00"))
                .capacity(capacity)
                .build();
    }

    private TicketTier reservedTier(String id, String seatId) {
        return TicketTier.builder()
                .id(id)
                .name(id)
                .price(new BigDecimal("10.00"))
                .inventoryMode(InventoryMode.RESERVED_SEATING)
                .seats(List.of(seat(seatId, "A", "1")))
                .build();
    }

    private SeatDefinition seat(String id, String row, String number) {
        return SeatDefinition.builder()
                .seatId(id)
                .section("Main")
                .row(row)
                .number(number)
                .build();
    }
}
