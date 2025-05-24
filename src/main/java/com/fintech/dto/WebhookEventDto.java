package com.fintech.dto;

import com.fintech.domain.WebhookEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class WebhookEventDto {
    private UUID id;
    private String eventType;
    private String payload;
    private String source;
    private WebhookEvent.EventStatus status;
    private String errorMessage;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public WebhookEvent.EventStatus getStatus() {
        return status;
    }

    public void setStatus(WebhookEvent.EventStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static WebhookEventDto fromEntity(WebhookEvent event) {
        WebhookEventDto dto = new WebhookEventDto();
        dto.setId(event.getId());
        dto.setEventType(event.getEventType());
        dto.setPayload(event.getPayload());
        dto.setSource(event.getSource());
        dto.setStatus(event.getStatus());
        dto.setErrorMessage(event.getErrorMessage());
        dto.setProcessedAt(event.getProcessedAt());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }
}
