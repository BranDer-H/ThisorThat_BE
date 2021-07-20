package com.thisorthat.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

import static com.thisorthat.chatting.MessageType.*;

@Component
@Log4j2
public class WebChatHandler extends TextWebSocketHandler {
    private static final String TAG = WebChatHandler.class.getSimpleName();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(TAG + ".afterConnectionEstablished "  + session + " client connected");
        super.afterConnectionEstablished(session);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(TAG + ".handleTestMessage " + session + " " + message);
        super.handleTextMessage(session, message);

        Gson gson = new Gson();
        ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);

        ChatRoom chatRoom = ChatRoom.getInstance();

        if (chatMessage.getMessageType() == MessageType.JOIN) {
            if(chatRoom.isDuplicateName(chatMessage.getName())){
                chatMessage = new ChatMessage("System", MessageType.ERROR, "001", System.currentTimeMillis(), "");
                chatRoom.sendMessage(chatMessage, session);
            } else {

                TextMessage textMessage = new TextMessage(gson.toJson(new ChatMessage("System", MessageType.PARTICIPANTS, chatRoom.getParticipantsName(), System.currentTimeMillis(), "")));
                session.sendMessage(textMessage);

                chatRoom.addParticipant(chatMessage.getName(), session);
                chatMessage = new ChatMessage("System", MessageType.JOIN, chatMessage.getName(), chatMessage.getTimestamp(), chatMessage.getColor());

                chatRoom.sendMessageToAll(chatMessage);
            }
        } else {
            chatRoom.sendMessageToAll(chatMessage);
        }
        return;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session + " 클라이언트 접속 해제. Status : " + status);
        super.afterConnectionClosed(session, status);
        String userName = ChatRoom.getInstance().getParticipantNameBySession(session);
        ChatRoom.getInstance().removeUser(session);

        ChatRoom.getInstance().sendMessageToAll(new ChatMessage("System", MessageType.LEAVE, userName, System.currentTimeMillis(), ""));
    }
}
