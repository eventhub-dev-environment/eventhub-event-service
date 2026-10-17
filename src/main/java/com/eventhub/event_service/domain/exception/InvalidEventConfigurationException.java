package com.eventhub.event_service.domain.exception;

public class InvalidEventConfigurationException extends RuntimeException {

    public InvalidEventConfigurationException(String message) {
        super(message);
    }
}
