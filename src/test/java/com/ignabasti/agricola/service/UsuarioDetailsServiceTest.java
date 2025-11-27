package com.ignabasti.agricola.service;

import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void loadUserByUsername_DebeRetornarUserDetailsCuandoExiste() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@correo.com");
        usuario.setContrasena("1234");

        Mockito.when(usuarioRepository.findByCorreo("test@correo.com"))
                .thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("test@correo.com");

        // Assert
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("test@correo.com", userDetails.getUsername());
        Assertions.assertEquals("1234", userDetails.getPassword());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        Mockito.verify(usuarioRepository).findByCorreo("test@correo.com");
    }

    @Test
    void loadUserByUsername_DebeLanzarExcepcionCuandoNoExiste() {
        Mockito.when(usuarioRepository.findByCorreo("noexiste@correo.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername("noexiste@correo.com")
        );

        Mockito.verify(usuarioRepository).findByCorreo("noexiste@correo.com");
    }
}