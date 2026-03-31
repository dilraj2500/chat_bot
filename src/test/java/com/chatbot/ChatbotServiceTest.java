package com.chatbot;

import com.chatbot.model.BotResponse;
import com.chatbot.model.IncomingMessage;
import com.chatbot.model.MessageLog;
import com.chatbot.repository.MessageLogRepository;
import com.chatbot.service.ChatbotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private MessageLogRepository messageLogRepository;

    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() {
        chatbotService = new ChatbotService(messageLogRepository);
    }

    @Test
    void testHiReply() {
        IncomingMessage msg = new IncomingMessage("+911234567890", "Hi", null);
        BotResponse response = chatbotService.processMessage(msg);
        assertEquals("Hello! 👋 How can I help you today?", response.getReply());
        assertEquals("sent", response.getStatus());
        verify(messageLogRepository, times(1)).save(any(MessageLog.class));
    }

    @Test
    void testHiCaseInsensitive() {
        IncomingMessage msg = new IncomingMessage("+911234567890", "HI", null);
        BotResponse response = chatbotService.processMessage(msg);
        assertEquals("Hello! 👋 How can I help you today?", response.getReply());
    }

    @Test
    void testByeReply() {
        IncomingMessage msg = new IncomingMessage("+911234567890", "Bye", null);
        BotResponse response = chatbotService.processMessage(msg);
        assertEquals("Goodbye! 👋 Have a great day!", response.getReply());
    }

    @Test
    void testUnknownMessage() {
        IncomingMessage msg = new IncomingMessage("+911234567890", "random text", null);
        BotResponse response = chatbotService.processMessage(msg);
        assertEquals("Sorry, I didn't understand that. Type 'help' to see available commands.", response.getReply());
    }

    @Test
    void testLogsAreRecorded() {
        chatbotService.processMessage(new IncomingMessage("+91111", "Hi", null));
        chatbotService.processMessage(new IncomingMessage("+91222", "Bye", null));
        verify(messageLogRepository, times(2)).save(any(MessageLog.class));
    }

    @Test
    void testClearLogs() {
        when(messageLogRepository.count()).thenReturn(1L);
        chatbotService.clearLogs();
        verify(messageLogRepository, times(1)).deleteAll();
    }

    @Test
    void testGetAllLogsReturnsNewestFirst() {
        MessageLog older = new MessageLog(1L, "+91111", "Hi", "Hello! 👋 How can I help you today?", "2026-03-31 10:00:00");
        MessageLog newer = new MessageLog(2L, "+91222", "Bye", "Goodbye! 👋 Have a great day!", "2026-03-31 10:05:00");
        when(messageLogRepository.findAll()).thenReturn(List.of(older, newer));

        List<MessageLog> logs = chatbotService.getAllLogs();
        assertEquals(2, logs.size());
        assertEquals(2L, logs.get(0).getId());
        assertEquals(1L, logs.get(1).getId());
    }

    @Test
    void testSavedLogContainsIncomingValues() {
        IncomingMessage msg = new IncomingMessage("+919999999999", "hello", null);
        chatbotService.processMessage(msg);

        ArgumentCaptor<MessageLog> captor = ArgumentCaptor.forClass(MessageLog.class);
        verify(messageLogRepository).save(captor.capture());
        MessageLog saved = captor.getValue();

        assertEquals("+919999999999", saved.getFromNumber());
        assertEquals("hello", saved.getReceivedMessage());
        assertEquals("Hey there! 😊 What can I do for you?", saved.getBotReply());
        assertTrue(saved.getLoggedAt() != null && !saved.getLoggedAt().isBlank());
    }
}
