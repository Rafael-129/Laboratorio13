package com.tecsup.laboratorio13.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**") // Permitir POST en H2
                .disable()
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()) // Permitir frames para H2
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/api-docs",
                    "/js/**", "/css/**", "/images/**",
                    "/h2-console/**" // <-- Permitir acceso a H2 Console
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/index.html", true) 
                .permitAll()
            )
            .logout(logout -> logout.permitAll());
        return http.build();
    }
}

