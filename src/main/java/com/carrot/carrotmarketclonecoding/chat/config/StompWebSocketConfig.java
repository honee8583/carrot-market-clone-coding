package com.carrot.carrotmarketclonecoding.chat.config;

import com.carrot.carrotmarketclonecoding.chat.component.WebSocketErrorHandler;
import com.carrot.carrotmarketclonecoding.chat.component.WebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.rabbitmq.host}")
    private String host;

    @Value("${websocket.rabbitmq.port}")
    private String port;

    @Value("${websocket.rabbitmq.username}")
    private String username;

    @Value("${websocket.rabbitmq.password}")
    private String password;

    @Value("${websocket.origin}")
    private String origin;

    private static final String BROKER_TOPIC_PREFIX = "/topic";
    private static final String BROKER_QUEUE_PREFIX = "/queue";
    private static final String PUBLISH_PREFIX = "/publish";
    private static final String CONNECT_URL = "/ws-connect";

    private final WebSocketInterceptor interceptor;
    private final WebSocketErrorHandler errorHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay(BROKER_TOPIC_PREFIX, BROKER_QUEUE_PREFIX)
                .setRelayHost(host)
                .setRelayPort(Integer.parseInt(port))
                .setClientLogin(username)
                .setClientPasscode(password);
        config.setApplicationDestinationPrefixes(PUBLISH_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(CONNECT_URL)
                .setAllowedOrigins(origin);
        registry.setErrorHandler(errorHandler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(interceptor);
    }
}
