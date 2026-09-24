package org.springframework.samples.petclinic.security;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize method-level security
@ConditionalOnProperty(name = "petclinic.security.enable", havingValue = "true")
public class BasicAuthenticationConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);
        // @formatter:on
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("select username,password,enabled from users where username=?");
        users.setAuthoritiesByUsernameQuery("select username,role from roles where username=?");
        return users;
    }
}
