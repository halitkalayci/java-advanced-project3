package com.smartorder.bff.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Configuration with LoadBalancer and OAuth2 Token Relay.
 * <p>
 * - LoadBalanced WebClient builder for service discovery
 * - Automatically attaches access token to outgoing requests
 * - Base URL: lb://gateway
 */
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    WebClient webClient(
            @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder builder,
            ReactiveClientRegistrationRepository clients,
            ServerOAuth2AuthorizedClientRepository authorizedClients) {

        var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clients, authorizedClients);
        oauth.setDefaultOAuth2AuthorizedClient(true); // attach current user's access token

        return builder
                .baseUrl("lb://gateway")
                .filter(oauth)
                .build();
    }
}

