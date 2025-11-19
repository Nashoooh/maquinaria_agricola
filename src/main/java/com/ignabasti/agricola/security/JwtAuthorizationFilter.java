package com.ignabasti.agricola.security;

import io.jsonwebtoken.*;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    
    @Resource
    private JwtService jwtService;
    
    private void setAuthentication(Claims claims) {
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    // ⚙️ 1️⃣ MÉTODO MODIFICADO → ahora busca token en Header o Cookie
    private Optional<String> getToken(HttpServletRequest request) {
        // Primero intenta desde el Header
        String authenticationHeader = request.getHeader("Authorization");
        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer")) {
            String unverifiedToken = authenticationHeader.replace("Bearer", "").trim();
            if (!unverifiedToken.isEmpty()) {
                log.info("🔐 Token obtenido desde Header");
                return Optional.of(unverifiedToken);
            }
        }

        // 👉 NUEVO: intenta desde Cookie "jwt"
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    log.info("🍪 Token obtenido desde Cookie");
                    return Optional.of(cookie.getValue());
                }
            }
        }

        return Optional.empty();
    }

    // 🚀 Filtro principal
    @Override
    @SuppressWarnings("null")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
        log.info("JwtAuthorizationFilter doFilter {}", request.getRequestURI());

        try {
            String uri = request.getRequestURI();
            if (uri.startsWith("/api/auth/") ||
                uri.startsWith("/inicio") ||
                uri.startsWith("/registro") ||
                uri.startsWith("/login") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/img/")) {
                log.info("🔓 Ruta pública: {}", uri);
                filterChain.doFilter(request, response);
                return;
            }

            Optional<String> optionalUnverifiedToken = getToken(request);
            if (optionalUnverifiedToken.isEmpty()) {
                throw new MalformedJwtException("Invalid JWT token");
            }

            String unverifiedToken = optionalUnverifiedToken.get();

            // ⚙️ 2️⃣ NUEVO: decodificar si el token viene Base64 (por el '=' que mencionaste)
            if (!unverifiedToken.startsWith("eyJ")) {
                try {
                    unverifiedToken = new String(Base64.getDecoder().decode(unverifiedToken), StandardCharsets.UTF_8);
                    log.info("🧩 Token decodificado desde Base64");
                } catch (Exception e) {
                    log.warn("⚠️ Error al decodificar Base64: {}", e.getMessage());
                }
            }

            Claims claims = jwtService.isTokenValid(unverifiedToken);
            log.info("✅ Token válido para usuario: {}", claims.getSubject());
            setAuthentication(claims);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            log.error("❌ JWT inválido: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}