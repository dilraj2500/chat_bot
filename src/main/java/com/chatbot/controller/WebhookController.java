package com.chatbot.controller;

import com.chatbot.model.BotResponse;
import com.chatbot.model.IncomingMessage;
import com.chatbot.model.MessageLog;
import com.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final ChatbotService chatbotService;

    public WebhookController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }


    @PostMapping
    public ResponseEntity<BotResponse> receiveMessage(@Valid @RequestBody IncomingMessage incoming) {
        BotResponse response = chatbotService.processMessage(incoming);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/logs")
    public ResponseEntity<List<MessageLog>> getLogs() {
        return ResponseEntity.ok(chatbotService.getAllLogs());
    }


    @DeleteMapping("/logs")
    public ResponseEntity<Map<String, String>> clearLogs() {
        chatbotService.clearLogs();
        return ResponseEntity.ok(Map.of("message", "All logs cleared successfully."));
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "WhatsApp Chatbot Backend",
                "version", "1.0.0"
        ));
    }
}
