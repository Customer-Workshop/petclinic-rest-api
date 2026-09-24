package org.springframework.samples.petclinic.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Starting from Spring Boot 2, if Spring Security is present, endpoints are secured by default
 * using Spring Security’s content-negotiation strategy.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "petclinic.security.enable", havingValue = "false")
public class DisableSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable);
        // @formatter:on
        return http.build();
    }
}
