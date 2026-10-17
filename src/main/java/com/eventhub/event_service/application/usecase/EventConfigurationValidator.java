package com.eventhub.event_service.application.usecase;

import com.eventhub.event_service.domain.model.Event;
import com.eventhub.event_service.domain.model.EventSession;
import com.eventhub.event_service.domain.model.InventoryMode;
import com.eventhub.event_service.domain.model.RegistrationField;
import com.eventhub.event_service.domain.model.SeatDefinition;
import com.eventhub.event_service.domain.model.TicketTier;
import com.eventhub.event_service.domain.exception.InvalidEventConfigurationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class EventConfigurationValidator {

    private static final Pattern FIELD_KEY = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,63}$");
    private static final Set<String> REGISTRATION_FIELD_TYPES = Set.of("TEXT", "NUMBER", "ENUM", "BOOLEAN");

    public void normalizeAndValidate(Event event) {
        if (event.getEventType() == null || event.getEventType().isBlank()) {
            event.setEventType("OTHER");
        } else {
            event.setEventType(event.getEventType().trim().toUpperCase(Locale.ROOT));
            if (!event.getEventType().matches("^[A-Z][A-Z0-9_]{0,49}$")) {
                reject("El tipo de evento debe ser una clave alfanumérica en mayúsculas.");
            }
        }

        List<EventSession> sessions = event.getSessions();
        boolean hasSessions = sessions != null && !sessions.isEmpty();
        List<TicketTier> eventTiers = event.getTicketTiers();
        boolean hasEventTiers = eventTiers != null && !eventTiers.isEmpty();

        if (hasSessions == hasEventTiers) {
            reject("Defina tarifas para una sola sesión o configure una o más sesiones, pero no ambas formas.");
        }

        if (hasSessions) {
            Set<String> sessionIds = new HashSet<>();
            OffsetDateTime earliestSession = null;
            for (EventSession session : sessions) {
                if (session == null) {
                    reject("La configuración contiene una sesión vacía.");
                }
                if (session.getId() == null || session.getId().isBlank()) {
                    session.setId(UUID.randomUUID().toString());
                }
                if (!sessionIds.add(session.getId())) {
                    reject("Los identificadores de sesión deben ser únicos dentro del evento.");
                }
                validateSession(session);
                if (earliestSession == null || session.getStartsAt().isBefore(earliestSession)) {
                    earliestSession = session.getStartsAt();
                }
            }
            if (event.getEventDate() == null) {
                event.setEventDate(earliestSession.toLocalDateTime());
            }
        } else {
            if (event.getEventDate() == null || !event.getEventDate().isAfter(LocalDateTime.now())) {
                reject("La fecha del evento debe ser posterior a la fecha actual.");
            }
            validateTicketTiers(eventTiers);
        }

        validateRegistrationFields(event.getRegistrationFields());
    }

    private void validateSession(EventSession session) {
        if (session.getName() == null || session.getName().isBlank()) {
            reject("Cada sesión debe tener un nombre.");
        }
        if (session.getStartsAt() == null || session.getEndsAt() == null
                || !session.getStartsAt().isAfter(OffsetDateTime.now())
                || !session.getEndsAt().isAfter(session.getStartsAt())) {
            reject("Cada sesión debe tener inicio futuro y fin posterior al inicio.");
        }
        if (session.getTicketTiers() == null || session.getTicketTiers().isEmpty()) {
            reject("Cada sesión debe definir al menos una tarifa.");
        }
        validateTicketTiers(session.getTicketTiers());
    }

    private void validateTicketTiers(List<TicketTier> tiers) {
        Set<String> tierIds = new HashSet<>();
        Set<String> sessionSeatIds = new HashSet<>();
        for (TicketTier tier : tiers) {
            if (tier == null || tier.getId() == null || tier.getId().isBlank()
                    || !tierIds.add(tier.getId())
                    || tier.getName() == null || tier.getName().isBlank()
                    || tier.getPrice() == null || tier.getPrice().signum() < 0) {
                reject("Cada tarifa debe tener identificador y nombre únicos, y un precio no negativo.");
            }
            if (tier.getInventoryMode() == null) {
                tier.setInventoryMode(InventoryMode.GENERAL_ADMISSION);
            }

            List<SeatDefinition> seats = tier.getSeats();
            if (tier.getInventoryMode() == InventoryMode.RESERVED_SEATING) {
                if (seats == null || seats.isEmpty()) {
                    reject("Las tarifas con asientos numerados deben definir sus asientos.");
                }
                Set<String> tierSeatIds = new HashSet<>();
                for (SeatDefinition seat : seats) {
                    if (seat == null || seat.getSeatId() == null || seat.getSeatId().isBlank()
                            || !tierSeatIds.add(seat.getSeatId())
                            || !sessionSeatIds.add(seat.getSeatId())) {
                        reject("Cada asiento debe tener un identificador único dentro de la sesión.");
                    }
                    if (seat.getSection() == null || seat.getSection().isBlank()
                            || seat.getRow() == null || seat.getRow().isBlank()
                            || seat.getNumber() == null || seat.getNumber().isBlank()) {
                        reject("Cada asiento debe definir sección, fila y número.");
                    }
                }
                if (tier.getCapacity() == null) {
                    tier.setCapacity(seats.size());
                }
                if (tier.getCapacity() != seats.size()) {
                    reject("La capacidad de una tarifa con asientos debe coincidir con la cantidad de asientos definidos.");
                }
            } else if (tier.getCapacity() == null || tier.getCapacity() <= 0
                    || (seats != null && !seats.isEmpty())) {
                reject("Las tarifas de cupo general deben tener capacidad positiva y no pueden definir asientos.");
            }
        }
    }

    private void validateRegistrationFields(List<RegistrationField> fields) {
        if (fields == null) {
            return;
        }
        Set<String> keys = new HashSet<>();
        for (RegistrationField field : fields) {
            if (field == null || field.getKey() == null || !FIELD_KEY.matcher(field.getKey()).matches()
                    || !keys.add(field.getKey())
                    || field.getLabel() == null || field.getLabel().isBlank()
                    || field.getType() == null
                    || !REGISTRATION_FIELD_TYPES.contains(field.getType())) {
                reject("Cada requisito de registro debe tener una clave única, etiqueta y tipo válidos.");
            }
            if ("ENUM".equals(field.getType())) {
                if (field.getOptions() == null || field.getOptions().isEmpty()) {
                    reject("Los campos ENUM deben incluir al menos una opción.");
                }
                Set<String> optionValues = new HashSet<>();
                for (var option : field.getOptions()) {
                    if (option == null || option.getLabel() == null || option.getLabel().isBlank()
                            || option.getValue() == null || option.getValue().isBlank()
                            || !optionValues.add(option.getValue())) {
                        reject("Las opciones de los campos ENUM deben tener etiqueta y valores únicos.");
                    }
                }
            } else if (field.getOptions() != null && !field.getOptions().isEmpty()) {
                reject("Solo los campos ENUM pueden definir opciones.");
            }
        }
    }

    private void reject(String message) {
        throw new InvalidEventConfigurationException(message);
    }
}
