package com.ignabasti.agricola.service;

import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationHelper {
    
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Obtiene el usuario autenticado actual desde el SecurityContext
     * @return Optional con el Usuario si está autenticado, Optional.empty() si no
     */
    public Optional<Usuario> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            String correoUsuario = authentication.getName();
            return usuarioRepository.findByCorreo(correoUsuario);
        }
        
        return Optional.empty();
    }
    
    /**
     * Obtiene el correo del usuario autenticado
     * @return El correo del usuario o null si no está autenticado
     */
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        
        return null;
    }
    
    /**
     * Obtiene el usuario autenticado actual desde el SecurityContext
     * Lanza una excepción si no hay usuario autenticado
     * @return El Usuario autenticado
     * @throws SecurityException si no hay usuario autenticado
     */
    public Usuario obtenerUsuarioAutenticado() {
        return getAuthenticatedUser()
                .orElseThrow(() -> new SecurityException("No hay usuario autenticado"));
    }
}
