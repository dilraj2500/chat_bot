package com.chatbot.service;

import com.chatbot.model.BotResponse;
import com.chatbot.model.IncomingMessage;
import com.chatbot.model.MessageLog;
import com.chatbot.repository.MessageLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotService.class);

    private final MessageLogRepository messageLogRepository;

    public ChatbotService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    private static final Map<String, String> REPLY_MAP = Map.of(
            "hi",           "Hello bhai! 👋 Kaise madad kar sakta hoon?",
            "hello",        "Hey! 😊 Bol kya kaam hai?",
            "bye",          "Chalta hoon bhai! 👋 Phir milenge!",
            "help",         "Bol bhai: Hi, Hello, Bye, Thanks, ya Kya haal hai?",
            "thanks",       "Koi baat nahi bhai! 😊",
            "thank you",    "Arre yaar, mention not! 😊",
            "kya haal hai", "Sab badiya bhai! 🤖 Tu bata?"
    );

    private static final String DEFAULT_REPLY =
            "Samjha nahi bhai! 'help' type karo commands dekhne ke liye.";

    public BotResponse processMessage(IncomingMessage incoming) {
        String now = currentTimestamp();

        log.info("📩 From: {} | Message: {}", incoming.getFrom(), incoming.getMessage());

        String reply = generateReply(incoming.getMessage());

        log.info("🤖 Reply: {}", reply);

        MessageLog logEntry = new MessageLog(
                null,
                incoming.getFrom(),
                incoming.getMessage(),
                reply,
                now
        );
        messageLogRepository.save(logEntry);

        return new BotResponse(incoming.getFrom(), reply, "sent", now);
    }

    private String generateReply(String message) {
        if (message == null || message.isBlank()) return DEFAULT_REPLY;
        return REPLY_MAP.getOrDefault(message.trim().toLowerCase(), DEFAULT_REPLY);
    }

    public List<MessageLog> getAllLogs() {
        return messageLogRepository.findAll();
    }

    public void clearLogs() {
        messageLogRepository.deleteAll();
        log.info("🗑️ Saare logs delete ho gaye!");
    }

    private String currentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}