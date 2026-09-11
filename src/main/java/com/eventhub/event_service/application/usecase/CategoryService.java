package com.eventhub.event_service.application.usecase;

import com.eventhub.event_service.domain.model.Category;
import com.eventhub.event_service.domain.port.in.ManageCategoryUseCase;
import com.eventhub.event_service.domain.port.out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ManageCategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;

    @Override
    public Category createCategory(Category category) {
        if (category.getActive() == null) {
            category.setActive(true);
        }
        return categoryRepositoryPort.save(category);
    }

    @Override
    public Optional<Category> getById(String id) {
        return categoryRepositoryPort.findById(id);
    }

    @Override
    public List<Category> getAllActive() {
        return categoryRepositoryPort.findAllActive();
    }

    @Override
    public Category updateCategory(String id, Category category) {
        return categoryRepositoryPort.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    existing.setDescription(category.getDescription());
                    if (category.getActive() != null) {
                        existing.setActive(category.getActive());
                    }
                    return categoryRepositoryPort.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }
}