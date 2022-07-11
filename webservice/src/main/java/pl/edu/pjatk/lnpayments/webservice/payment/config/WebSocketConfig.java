package pl.edu.pjatk.lnpayments.webservice.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import pl.edu.pjatk.lnpayments.webservice.auth.interceptor.AuthChannelInterceptor;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.NOTIFICATION_WS_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.PAYMENTS_WS_PATH;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor channelInterceptor;

    public WebSocketConfig(AuthChannelInterceptor channelInterceptor) {
        this.channelInterceptor = channelInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(PAYMENTS_WS_PATH).setAllowedOriginPatterns("*");
        registry.addEndpoint(NOTIFICATION_WS_PATH).setAllowedOriginPatterns("*");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
