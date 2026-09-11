package com.eventhub.event_service.infrastructure.adapter.out.persistence;

import com.eventhub.event_service.infrastructure.adapter.out.persistence.entity.CategoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMongoCategoryRepository extends MongoRepository<CategoryDocument, String> {
    List<CategoryDocument> findByActiveTrue();
}