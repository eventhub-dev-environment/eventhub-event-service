package com.eventhub.event_service.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventAccessPolicy {

    public void requireTenantStaff(String tenantId, String activeTenantId, String tenantRoles, String userRoles) {
        if (hasGlobalRole(userRoles, "PLATFORM_ADMIN")) {
            return;
        }
        Set<String> roles = parseRoles(tenantRoles);
        if (tenantId == null || !tenantId.equals(activeTenantId)
                || roles.stream().noneMatch(Set.of("TENANT_ADMIN", "EVENT_ORGANIZER")::contains)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos para este tenant");
        }
    }

    public void requirePlatformAdmin(String userRoles) {
        if (!hasGlobalRole(userRoles, "PLATFORM_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere PLATFORM_ADMIN");
        }
    }

    public boolean hasGlobalRole(String userRoles, String role) {
        return parseRoles(userRoles).contains(role);
    }

    private Set<String> parseRoles(String roles) {
        if (roles == null || roles.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .collect(Collectors.toSet());
    }
}
