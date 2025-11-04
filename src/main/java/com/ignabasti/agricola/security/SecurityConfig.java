package com.ignabasti.agricola.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SecurityConfig {
    
    @Autowired
    JwtAuthorizationFilter JwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    // vistas públicas y recursos estáticos
                    .requestMatchers("/inicio", "/login", "/registro", "/css/**", "/js/**", "/images/**").permitAll()
                    // endpoints públicos de autenticación
                    .requestMatchers("/api/auth/**").permitAll()
                    // cualquier otra ruta requiere autenticación con JWT
                    .anyRequest().authenticated()
            )
            // sin sesiones, usamos JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // agregamos el filtro JWT
            .addFilterBefore(JwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();

        // try {
        //     log.info("Iniciando security");
        //     http
        //         .csrf(AbstractHttpConfigurer::disable)
        //         .authorizeHttpRequests(auth -> auth
        //             .requestMatchers("/api/auth/**").permitAll()
        //             .anyRequest().authenticated())
        //         .addFilterAfter(JwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        // } catch (Exception e) {
            
        //     log.error("Error configuring security filter chain: {}", e.getMessage());
        //     e.printStackTrace();
        // }
        // return http.build();
    }

    // ✅ Este bean resuelve tu error
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}