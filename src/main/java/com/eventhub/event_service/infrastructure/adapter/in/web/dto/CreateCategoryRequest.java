package com.eventhub.event_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    private String description;
    private Boolean active;
}