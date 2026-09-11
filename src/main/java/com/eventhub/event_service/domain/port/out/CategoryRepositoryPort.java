package com.eventhub.event_service.domain.port.out;

import com.eventhub.event_service.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(String id);
    List<Category> findAllActive();
}