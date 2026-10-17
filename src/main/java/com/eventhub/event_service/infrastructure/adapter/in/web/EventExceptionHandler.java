package com.eventhub.event_service.infrastructure.adapter.in.web;

import com.eventhub.event_service.domain.exception.InvalidEventConfigurationException;
import com.eventhub.event_service.infrastructure.adapter.in.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler(InvalidEventConfigurationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidEventConfiguration(
            InvalidEventConfigurationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(exception.getMessage(), null));
    }
}
