package com.eventhub.event_service.infrastructure.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventAccessPolicyTest {

    private final EventAccessPolicy policy = new EventAccessPolicy();

    @Test
    void acceptsTenantStaffOnlyInTheirActiveTenant() {
        assertThatCode(() -> policy.requireTenantStaff(
                "tenant-a", "tenant-a", "EVENT_ORGANIZER", "CUSTOMER"))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> policy.requireTenantStaff(
                "tenant-a", "tenant-b", "EVENT_ORGANIZER", "CUSTOMER"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(((ResponseStatusException) exception).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void platformAdminCanManageAnyTenant() {
        assertThatCode(() -> policy.requireTenantStaff(
                "tenant-a", null, "", "PLATFORM_ADMIN"))
                .doesNotThrowAnyException();
    }
}
