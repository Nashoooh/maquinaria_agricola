package com.ignabasti.agricola.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http
            // Headers: Content Security Policy (CSP) Que corrije la vulnerabilidad de XSS ZAP #1
            .headers(headers -> headers
                .contentSecurityPolicy(
                    "default-src 'self'; " +
                    "script-src 'self'; " +
                    "style-src 'self'; " +    // quitar 'unsafe-inline' si moviste estilos a archivos .css
                    "img-src 'self' data:; " +
                    "font-src 'self'; " +
                    "connect-src 'self'; " +
                    "frame-ancestors 'none'; " +
                    "form-action 'self';"
                )
                .and()
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            )
            // .csrf(csrf -> csrf.disable())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")) // APIs JWT no requieren CSRF
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/maquinaria/**")) // APIs maquinaria JWT no requieren CSRF
            )
            .authorizeHttpRequests(auth -> auth
                    // vistas públicas y recursos estáticos
                    .requestMatchers("/favicon.ico", "/inicio", "/login", "/registro", "/css/**", "/js/**", "/images/**").permitAll()
                    // endpoints públicos de autenticación
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/maquinaria/**").permitAll()
                    // cualquier otra ruta requiere autenticación con JWT
                    .anyRequest().authenticated()
            )
            // sin sesiones, usamos JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // agregamos el filtro JWT
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}