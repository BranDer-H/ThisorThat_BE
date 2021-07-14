package com.thisorthat.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String name;
    private MessageType messageType;
    private String content;
    private long timestamp;
    private String color;
}
