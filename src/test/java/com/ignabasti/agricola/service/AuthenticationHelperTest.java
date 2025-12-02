package com.ignabasti.agricola.service;

import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationHelperTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthenticationHelper authenticationHelper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setCorreo("test@mail.com");
        usuario.setNombre("Test User");

        SecurityContextHolder.setContext(securityContext);
    }

    // -------------------------------------------------------------
    // getAuthenticatedUser()
    // -------------------------------------------------------------
    @Test
    void getAuthenticatedUser_autenticado() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario.getCorreo());
        when(authentication.getName()).thenReturn(usuario.getCorreo());
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = authenticationHelper.getAuthenticatedUser();

        assertTrue(result.isPresent());
        assertEquals(usuario.getCorreo(), result.get().getCorreo());
    }

    @Test
    void getAuthenticatedUser_noAutenticado() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Optional<Usuario> result = authenticationHelper.getAuthenticatedUser();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAuthenticatedUser_anonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        Optional<Usuario> result = authenticationHelper.getAuthenticatedUser();

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------
    // getAuthenticatedUserEmail()
    // -------------------------------------------------------------
    @Test
    void getAuthenticatedUserEmail_autenticado() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario.getCorreo());
        when(authentication.getName()).thenReturn(usuario.getCorreo());

        String email = authenticationHelper.getAuthenticatedUserEmail();

        assertEquals(usuario.getCorreo(), email);
    }

    @Test
    void getAuthenticatedUserEmail_noAutenticado() {
        when(securityContext.getAuthentication()).thenReturn(null);

        String email = authenticationHelper.getAuthenticatedUserEmail();

        assertNull(email);
    }

    // -------------------------------------------------------------
    // obtenerUsuarioAutenticado()
    // -------------------------------------------------------------
    @Test
    void obtenerUsuarioAutenticado_exitoso() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario.getCorreo());
        when(authentication.getName()).thenReturn(usuario.getCorreo());
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));

        Usuario result = authenticationHelper.obtenerUsuarioAutenticado();

        assertEquals(usuario.getCorreo(), result.getCorreo());
    }

    @Test
    void obtenerUsuarioAutenticado_noAutenticado_lanzaExcepcion() {
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityException exception = assertThrows(SecurityException.class,
                () -> authenticationHelper.obtenerUsuarioAutenticado());

        assertEquals("No hay usuario autenticado", exception.getMessage());
    }

    // AuthenticatedUser tests
    @Test
    void getAuthenticatedUser_authenticationNotAuthenticated_returnsEmpty() {
        // Preparar SecurityContext con authentication no autenticado
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        Optional<Usuario> result = authenticationHelper.getAuthenticatedUser();

        assertEquals(Optional.empty(), result);
    }

    @Test
    void getAuthenticatedUser_authenticationAnonymous_returnsEmpty() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        Optional<Usuario> result = authenticationHelper.getAuthenticatedUser();

        assertEquals(Optional.empty(), result);
    }

    @Test
    void getAuthenticatedUser_authenticatedUser_callsRepository() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("usuario@mail.com");
        when(authentication.getName()).thenReturn("usuario@mail.com");

        Usuario usuario = new Usuario();
        usuario.setCorreo("usuario@mail.com");
        when(usuarioRepository.findByCorreo("usuario@mail.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = authenticationHelper.getAuthenticatedUser();

        assertEquals(usuario, result.get());
        verify(usuarioRepository).findByCorreo("usuario@mail.com");
    }

    // AuthenticatedUserEmail tests
    @Test
    void getAuthenticatedUserEmail_authenticationIsNull_returnsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        String result = authenticationHelper.getAuthenticatedUserEmail();
        assertEquals(null, result);
    }

    @Test
    void getAuthenticatedUserEmail_notAuthenticated_returnsNull() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = authenticationHelper.getAuthenticatedUserEmail();
        assertEquals(null, result);
    }

    @Test
    void getAuthenticatedUserEmail_anonymousUser_returnsNull() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        String result = authenticationHelper.getAuthenticatedUserEmail();
        assertEquals(null, result);
    }

    @Test
    void getAuthenticatedUserEmail_authenticatedUser_returnsEmail() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("usuario@mail.com");
        when(authentication.getName()).thenReturn("usuario@mail.com");

        String result = authenticationHelper.getAuthenticatedUserEmail();
        assertEquals("usuario@mail.com", result);
    }
}
