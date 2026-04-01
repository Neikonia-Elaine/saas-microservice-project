package org.microservice.tenantservice.config;

import org.microservice.tenantservice.security.AdminAuthenticationProvider;
import org.microservice.tenantservice.security.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final AdminAuthenticationProvider adminAuthenticationProvider;

    public SecurityConfig(UserAuthenticationProvider userAuthenticationProvider,
                          AdminAuthenticationProvider adminAuthenticationProvider) {
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.adminAuthenticationProvider = adminAuthenticationProvider;
    }

    @Bean
    @Qualifier("userAuthManager")
    public AuthenticationManager userAuthenticationManager() {
        return new ProviderManager(List.of(userAuthenticationProvider));
    }

    @Bean
    @Qualifier("adminAuthManager")
    public AuthenticationManager adminAuthenticationManager() {
        return new ProviderManager(List.of(adminAuthenticationProvider));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
