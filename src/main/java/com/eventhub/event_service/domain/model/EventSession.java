package com.eventhub.event_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSession {
    private String id;
    private String name;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private List<TicketTier> ticketTiers;
}
