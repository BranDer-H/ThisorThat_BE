package com.thisorthat.chatting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Getter
@Setter
public class ChatRoom {
    private static final String TAG = ChatRoom.class.getSimpleName();
    private Map<String, WebSocketSession> participants = new HashMap<>();
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private static ChatRoom chatRoom = null;

    private ChatRoom(){}

    public static ChatRoom getInstance(){
        log.info(TAG + ".getInstance()");

        if(chatRoom == null)
            return chatRoom = new ChatRoom();
        return chatRoom;
    }


    public void addParticipant(String name, WebSocketSession session) {
        log.info(TAG + ".addParticipant()");

        participants.put(name, session);
    }

    public void sendMessageToAll(ChatMessage chatMessage, ObjectMapper objectMapper) {
        log.info(TAG+".sendMessageToAll()");

        log.info("Sending message to " + participants.size() + " members");
        log.info("From " + chatMessage.getName());
        log.info("content: " + chatMessage.getContent());
        for(String name : participants.keySet()){
            log.info("To " + name);
            try {
                TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
                participants.get(name).sendMessage(textMessage);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDuplicateName(String name) {
        if(participants.containsKey(name))
            return true;
        return false;
    }

    public void sendMessage(ChatMessage chatMessage, ObjectMapper objectMapper, WebSocketSession session) {
        TextMessage textMessage = null;
        try {
            textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
            session.sendMessage(textMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
