package com.eventhub.event_service.infrastructure.adapter.out.persistence;

import com.eventhub.event_service.infrastructure.adapter.out.persistence.entity.EventDocument;
import com.eventhub.event_service.domain.model.EventStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMongoEventRepository extends MongoRepository<EventDocument, String> {
    List<EventDocument> findByTenantId(String tenantId);
    List<EventDocument> findByStatus(EventStatus status);
}