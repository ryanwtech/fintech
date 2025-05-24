package com.fintech.web;

import com.fintech.dto.WebhookEventDto;
import com.fintech.dto.WebhookPayloadDto;
import com.fintech.service.WebhookEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/webhooks")
@CrossOrigin(origins = "*")
public class WebhookController {

    @Autowired
    private WebhookEventService webhookEventService;

    @PostMapping("/mockbank")
    public ResponseEntity<String> receiveMockBankWebhook(@RequestBody WebhookPayloadDto payload) {
        try {
            // Create webhook event
            String payloadJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            var event = webhookEventService.createWebhookEvent("transactions.new", payloadJson, "mockbank");
            
            // Process asynchronously
            webhookEventService.processWebhookEvent(event.getId());
            
            return ResponseEntity.ok("Webhook received and queued for processing");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    public ResponseEntity<List<WebhookEventDto>> getWebhookEvents() {
        // This endpoint is for debugging/monitoring
        List<WebhookEventDto> events = webhookEventService.getPendingEventsAsDto();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/test/simulate")
    public ResponseEntity<String> simulateWebhook(@RequestBody WebhookPayloadDto payload) {
        try {
            // Create webhook event
            String payloadJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            var event = webhookEventService.createWebhookEvent("transactions.new", payloadJson, "test");
            
            // Process asynchronously
            webhookEventService.processWebhookEvent(event.getId());
            
            return ResponseEntity.ok("Test webhook simulated and queued for processing");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error simulating webhook: " + e.getMessage());
        }
    }
}
