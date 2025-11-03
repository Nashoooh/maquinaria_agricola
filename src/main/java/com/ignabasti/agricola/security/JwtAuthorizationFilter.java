package com.ignabasti.agricola.security;

import io.jsonwebtoken.*;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    
    @Resource
    private JwtService jwtService;
    
    private void setAuthentication(Claims claims) {
        //List<String> authorities = (List<String>) claims.get("authorities");
        // no se que hace esto realmente, pero parece que setea la autenticacion en el contexto de seguridad
        // al parecer el contexto se propaga a lo largo de la solicitud
        // y deberia poder obtenerse en los controladores (?)
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
    }
    
    private Optional<String> getToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader("Authorization");
        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer")) return Optional.empty();
        
        String unverifiedToken = request
            .getHeader("Authorization")
            .replace("Bearer", "")
            .trim();
        
        if (unverifiedToken.isEmpty()) return Optional.empty();
        
        return Optional.of(unverifiedToken);
    }
    
     //por aqui pasan todas las solicitudes, si todas
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        log.info("JwtAuthorizationFilter doFilter {}", request.getRequestURI());
        try {
            // if(request.getRequestURI().startsWith("/access/")) {
            //     log.info("Skipping JWT check for /access/ endpoint");
            //     filterChain.doFilter(request, response);
            //     return;
            // }
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

            // obtengo el token no verificado, podria ser un string vacio o nulo
            Optional<String> optionalUnverifiedToken = getToken(request);
            if(optionalUnverifiedToken.isEmpty()) {
                throw new MalformedJwtException("Invalid JWT token");
            }
            // si el token no esta vacio al menos podria no estar verificado
            String unverifiedToken = optionalUnverifiedToken.get();
            //verifico el token y obtengo los claims
            Claims claims = jwtService.isTokenValid(unverifiedToken);
            log.info("Valid token found!");
            // seteo la autenticacion en el contexto de seguridad (invetigar mas sobre esto)
            setAuthentication(claims);
            // continuo con la cadena de filtros
            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            log.error(e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}