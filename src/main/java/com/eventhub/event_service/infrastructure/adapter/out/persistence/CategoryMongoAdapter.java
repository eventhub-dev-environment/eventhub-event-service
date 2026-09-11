package com.eventhub.event_service.infrastructure.adapter.out.persistence;

import com.eventhub.event_service.domain.model.Category;
import com.eventhub.event_service.domain.port.out.CategoryRepositoryPort;
import com.eventhub.event_service.infrastructure.adapter.out.persistence.entity.CategoryDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryMongoAdapter implements CategoryRepositoryPort {

    private final SpringDataMongoCategoryRepository repository;

    @Override
    public Category save(Category category) {
        CategoryDocument doc = CategoryDocument.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(Boolean.TRUE.equals(category.getActive())) // Manejo seguro de Boolean
                .build();

        CategoryDocument saved = repository.save(doc);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Category> findById(String id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Category> findAllActive() {
        return repository.findByActiveTrue().stream()
                .map(this::mapToDomain)
                .toList();
    }

    private Category mapToDomain(CategoryDocument doc) {
        return Category.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .active(doc.getActive())
                .build();
    }
}