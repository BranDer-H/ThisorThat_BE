package com.thisorthat.chatting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

    public void sendMessageToAll(ChatMessage chatMessage) {
        log.info(TAG+".sendMessageToAll()");

        log.info("Sending message to " + participants.size() + " members");
        log.info("From " + chatMessage.getName());
        log.info("content: " + chatMessage.getContent());
        for(String name : participants.keySet()){
            log.info("To " + name);
            try {
                Gson gson = new Gson();
                TextMessage textMessage = new TextMessage(gson.toJson(chatMessage));
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

    public void sendMessage(ChatMessage chatMessage, WebSocketSession session) {
        TextMessage textMessage = null;
        try {
            Gson gson = new Gson();
            textMessage = new TextMessage(gson.toJson(chatMessage));
            session.sendMessage(textMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(WebSocketSession session) {
        String userName = getParticipantNameBySession(session);
        participants.remove(userName);
    }

    public String getParticipantNameBySession(WebSocketSession session) {
        String userName = null;
        for(Map.Entry<String, WebSocketSession> entry : participants.entrySet()){
            if(entry.getValue().equals(session)){
                userName = entry.getKey();
            }
        }
        return userName;
    }

    public String getParticipantsName() {
        return participants.keySet().toString();
    }
}
