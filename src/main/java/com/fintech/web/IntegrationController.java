package com.fintech.web;

import com.fintech.dto.BankConnectionDto;
import com.fintech.dto.LinkBankRequest;
import com.fintech.service.MockBankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/integrations")
@CrossOrigin(origins = "*")
public class IntegrationController {

    @Autowired
    private MockBankService mockBankService;

    @PostMapping("/mockbank/link")
    public ResponseEntity<BankConnectionDto> linkMockBank(
            @RequestParam UUID userId,
            @Valid @RequestBody LinkBankRequest request) {
        BankConnectionDto connection = mockBankService.linkBank(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(connection);
    }

    @GetMapping("/mockbank/connections")
    public ResponseEntity<List<BankConnectionDto>> getUserConnections(
            @RequestParam UUID userId) {
        List<BankConnectionDto> connections = mockBankService.getUserConnections(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/mockbank/connections/{connectionId}")
    public ResponseEntity<BankConnectionDto> getConnectionById(
            @RequestParam UUID userId,
            @PathVariable UUID connectionId) {
        BankConnectionDto connection = mockBankService.getConnectionById(userId, connectionId);
        return ResponseEntity.ok(connection);
    }

    @DeleteMapping("/mockbank/connections/{connectionId}")
    public ResponseEntity<Void> unlinkBank(
            @RequestParam UUID userId,
            @PathVariable UUID connectionId) {
        mockBankService.unlinkBank(userId, connectionId);
        return ResponseEntity.noContent().build();
    }
}
