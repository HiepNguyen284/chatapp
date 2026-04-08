package com.group4.chatapp.configs

import com.group4.chatapp.interceptors.WebSocketAuthenticationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig
@Autowired constructor(

    val webSocketInterceptor: WebSocketAuthenticationInterceptor,

    @Value($$"${websocket.relay_host}") val relayHost: String,
    @Value($$"${websocket.relay_port}") val relayPort: Int,

) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {

        registry
            .enableStompBrokerRelay("/topic", "/queue")
            .setRelayHost(relayHost)
            .setRelayPort(relayPort)

        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {

        registry.addEndpoint("/socket")
            .setAllowedOriginPatterns("*")
            .withSockJS()

        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(webSocketInterceptor)
    }
}
