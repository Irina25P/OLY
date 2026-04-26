package com.example.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${security.username-header}")
    private String usernameHeader;

    @Value("${security.role-header}")
    private String roleHeader;

    @Value("${websocket.endpoint}")
    private String websocketEndpoint;

    @Value("${websocket.application-prefix}")
    private String applicationPrefix;

    @Value("${websocket.broker-prefix}")
    private String brokerPrefix;

    @Value("${websocket.user-prefix}")
    private String userPrefix;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(websocketEndpoint)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(applicationPrefix);
        registry.enableSimpleBroker(brokerPrefix);
        registry.setUserDestinationPrefix(userPrefix);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthenticationInterceptor());
    }

    @Bean
    public ChannelInterceptor webSocketAuthenticationInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
                    return message;
                }

                String principalName = firstNativeHeader(accessor, usernameHeader);
                if (!StringUtils.hasText(principalName)) {
                    throw new IllegalArgumentException("Missing websocket authentication header: " + usernameHeader);
                }

                String role = firstNativeHeader(accessor, roleHeader);
                List<GrantedAuthority> authorities = StringUtils.hasText(role)
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        : List.of();

                Authentication authentication = new UsernamePasswordAuthenticationToken(principalName, null, authorities);
                accessor.setUser(authentication);
                return message;
            }
        };
    }

    private String firstNativeHeader(StompHeaderAccessor accessor, String headerName) {
        List<String> values = accessor.getNativeHeader(headerName);
        return (values == null || values.isEmpty()) ? null : values.getFirst();
    }
}
