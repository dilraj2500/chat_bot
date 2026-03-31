package com.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingMessage {

    @NotBlank(message = "Sender number ('from') cannot be blank")
    @JsonProperty("from")
    private String from;

    @NotBlank(message = "Message cannot be blank")
    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private String timestamp;
}
