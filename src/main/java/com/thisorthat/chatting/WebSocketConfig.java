package com.thisorthat.chatting;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private static final String TAG = WebSocketConfig.class.getSimpleName();

    private final WebChatHandler webChatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info(TAG + "registerWebSocketHandlers");

        /** postman으로 테스트 할 땐 아래 문장 수행 */
        registry.addHandler(webChatHandler, "/web/chat").setAllowedOrigins("*");

        /** 실제 Front와 통신에는 아래 문장 수행 */
        //registry.addHandler(webChatHandler, "/web/chat").setAllowedOrigins("*").withSockJS();

    }
}
