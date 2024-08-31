package com.manuel.ecommerce.configurations;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8081")
                .realm("eCommerce")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("eCommerce_login")
                .username("admin")
                .password("12345678")
                .scope("openid")
                .build();
    }

}
