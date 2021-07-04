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
import java.util.List;

@Log4j2
@Getter
@Setter
public class ChatRoom {
    private static final String TAG = ChatRoom.class.getSimpleName();
    private List<WebSocketSession> participants = new ArrayList<>();
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private static ChatRoom chatRoom = null;

    private ChatRoom(){}

    public static ChatRoom getInstance(){
        log.info(TAG + ".getInstance()");

        if(chatRoom == null)
            return chatRoom = new ChatRoom();
        return chatRoom;
    }


    public void addParticipant(WebSocketSession session) {
        log.info(TAG + ".addParticipant()");

        participants.add(session);
    }

    public void sendMessageToAll(ChatMessage chatMessage, ObjectMapper objectMapper) {
        log.info(TAG+".sendMessageToAll()");

        log.info("Sending message to " + participants.size() + " members");
        log.info("From " + chatMessage.getName());
        log.info("content: " + chatMessage.getContent());
        for(WebSocketSession session : participants){
            log.info("To " + session);
            try {
                TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
                session.sendMessage(textMessage);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
