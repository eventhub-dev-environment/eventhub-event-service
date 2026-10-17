# EventHub Event Service

## Tenant authorization

Create events through `POST /api/v1/events` with a Bearer token and `X-Tenant-ID` header. The active tenant must be one in which the user has `TENANT_ADMIN` or `EVENT_ORGANIZER`; the event's tenant is taken from the verified header, not from the request body.

Published events are readable to authenticated users. Draft, cancelled, and completed events and the tenant event listing require staff membership in the event's tenant. Branding changes require the same membership. Global category creation and updates are restricted to `PLATFORM_ADMIN`.
