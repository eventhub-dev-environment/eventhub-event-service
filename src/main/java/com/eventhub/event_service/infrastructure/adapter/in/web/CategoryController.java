package com.eventhub.event_service.infrastructure.adapter.in.web;

import com.eventhub.event_service.domain.model.Category;
import com.eventhub.event_service.domain.port.in.ManageCategoryUseCase;
import com.eventhub.event_service.infrastructure.adapter.in.web.dto.ApiResponse;
import com.eventhub.event_service.infrastructure.adapter.in.web.dto.CreateCategoryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ManageCategoryUseCase manageCategoryUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive())
                .build();

        Category created = manageCategoryUseCase.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Categoría creada exitosamente"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllActive() {
        List<Category> categories = manageCategoryUseCase.getAllActive();
        return ResponseEntity.ok(ApiResponse.success(categories, "Categorías activas recuperadas exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getById(@PathVariable String id) {
        return manageCategoryUseCase.getById(id)
                .map(category -> ResponseEntity.ok(ApiResponse.success(category, "Categoría encontrada")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Categoría no encontrada con ID: " + id, null)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable String id, @Valid @RequestBody CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive())
                .build();

        Category updated = manageCategoryUseCase.updateCategory(id, category);
        return ResponseEntity.ok(ApiResponse.success(updated, "Categoría actualizada exitosamente"));
    }
}