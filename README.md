# EventHub Event Service

## Tenant authorization

Create events through `POST /api/v1/events` with a Bearer token and `X-Tenant-ID` header. The active tenant must be one in which the user has `TENANT_ADMIN` or `EVENT_ORGANIZER`; the event's tenant is taken from the verified header, not from the request body.

Published events are readable to authenticated users. Draft, cancelled, and completed events and the tenant event listing require staff membership in the event's tenant. Branding changes require the same membership. Global category creation and updates are restricted to `PLATFORM_ADMIN`.

## Event sessions and inventory definitions

Events can keep the legacy single-occurrence `eventDate` and event-level `ticketTiers`, or define `sessions` with a separate schedule and ticket tiers for each occurrence; the two ticket configuration forms cannot be mixed. When sessions are supplied, `eventDate` is optional and defaults to the earliest session start. `eventType` is an extensible uppercase key such as `CINEMA`, `CONCERT`, or `RACE`; omitted values default to `OTHER`.

Each ticket tier has a fixed `capacity` and an `inventoryMode`:

- `GENERAL_ADMISSION` represents a capacity-only pool.
- `RESERVED_SEATING` includes a list of stable `seatId`, section, row, and number definitions; its capacity is derived from or must match the seat count.

Per-event `registrationFields` define participant data using `key`, `label`, `type` (`TEXT`, `NUMBER`, `ENUM`, or `BOOLEAN`), `required`, and optional `options` for `ENUM`. These definitions allow event-specific questions without hard-coding requirements for individual verticals.

Event Service owns the event/session/tier/seat-map definitions. Booking Service owns mutable availability, seat states, and holds; its relational inventory tables are the source of truth for stock. Availability and atomic holds are introduced in the following phase. The legacy tier `available` value is no longer returned or maintained by Event Service because it was not a reliable stock source.

Example multi-session request body (tenant comes from the verified `X-Tenant-ID` header):

```json
{
  "title": "Friday cinema",
  "categoryId": "cinema",
  "location": "Downtown",
  "eventType": "CINEMA",
  "sessions": [
    {
      "name": "7:00 PM",
      "startsAt": "2026-11-20T19:00:00-05:00",
      "endsAt": "2026-11-20T21:00:00-05:00",
      "ticketTiers": [
        {
          "id": "standard",
          "name": "Standard",
          "price": 12.50,
          "inventoryMode": "RESERVED_SEATING",
          "seats": [
            {
              "seatId": "screen-1-row-a-seat-1",
              "section": "Screen 1",
              "row": "A",
              "number": "1",
              "accessible": false
            }
          ]
        }
      ]
    }
  ],
  "registrationFields": []
}
```
