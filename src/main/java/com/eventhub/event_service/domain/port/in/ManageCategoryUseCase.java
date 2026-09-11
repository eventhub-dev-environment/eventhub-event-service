package com.eventhub.event_service.domain.port.in;

import com.eventhub.event_service.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface ManageCategoryUseCase {
    Category createCategory(Category category);
    Optional<Category> getById(String id);
    List<Category> getAllActive();
    Category updateCategory(String id, Category category);
}