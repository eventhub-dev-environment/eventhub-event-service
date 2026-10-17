package com.eventhub.event_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationField {
    private String key;
    private String label;
    private String type;
    private boolean required;
    private List<RegistrationFieldOption> options;
}
