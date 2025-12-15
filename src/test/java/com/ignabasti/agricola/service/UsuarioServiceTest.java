package com.ignabasti.agricola.service;

import com.ignabasti.agricola.dto.UsuarioDTO;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.UsuarioRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // ------------------------------------------------------------
    // registrarUsuario()
    // ------------------------------------------------------------
    @Test
    void registrarUsuario_debeRegistrarCorrectamente() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Juan");
        dto.setCorreo("test@correo.com");
        dto.setContrasena("1234");
        dto.setTelefono("111");
        dto.setDireccion("Calle 1");

        Mockito.when(usuarioRepository.findByCorreo("test@correo.com"))
                .thenReturn(Optional.empty());

        Mockito.when(passwordEncoder.encode("1234")).thenReturn("ENCODED");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1);
        usuarioGuardado.setCorreo("test@correo.com");

        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.registrarUsuario(dto);

        Assertions.assertEquals(1, resultado.getId());
        Mockito.verify(usuarioRepository).save(Mockito.any(Usuario.class));
    }

    @Test
    void registrarUsuario_debeLanzarErrorSiCorreoExiste() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setCorreo("duplicado@correo.com");

        Mockito.when(usuarioRepository.findByCorreo("duplicado@correo.com"))
                .thenReturn(Optional.of(new Usuario()));

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(dto)
        );
    }

    // ------------------------------------------------------------
    // actualizarPerfil()
    // ------------------------------------------------------------
    @Test
    void actualizarPerfil_todosLosCampos() {
        Usuario usuario = new Usuario();
        UsuarioDTO dto = UsuarioDTO.builder()
                .nombre("Juan")
                .telefono("123")
                .direccion("Calle 1")
                .cultivos("Trigo")
                .contrasena("1234")
                .build();

        when(passwordEncoder.encode("1234")).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.actualizarPerfil(usuario, dto);

        assertEquals("Juan", result.getNombre());
        assertEquals("123", result.getTelefono());
        assertEquals("Calle 1", result.getDireccion());
        assertEquals("Trigo", result.getCultivos());
        assertEquals("encoded", result.getContrasena());
    }

    @Test
    void actualizarPerfil_nombreYContrasenaNull() {
        Usuario usuario = new Usuario();
        UsuarioDTO dto = new UsuarioDTO();

        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.actualizarPerfil(usuario, dto);

        assertNull(result.getNombre());
        assertNull(result.getContrasena());
    }

    @Test
    void actualizarPerfil_nombreYContrasenaVacios() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Original");

        UsuarioDTO dto = UsuarioDTO.builder()
                .nombre("")
                .contrasena("")
                .build();

        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.actualizarPerfil(usuario, dto);

        assertEquals("Original", result.getNombre()); // no cambia
        assertNull(result.getContrasena());
    }

    @Test
    void actualizarPerfil_camposOpcionalesNull() {
        Usuario usuario = new Usuario();
        UsuarioDTO dto = new UsuarioDTO();

        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.actualizarPerfil(usuario, dto);

        assertNull(result.getTelefono());
        assertNull(result.getDireccion());
        assertNull(result.getCultivos());
    }


    // ------------------------------------------------------------
    // obtenerUsuarioPorCorreo()
    // ------------------------------------------------------------
    @Test
    void obtenerUsuarioPorCorreo_debeRetornarDTO() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setCorreo("test@correo.com");
        usuario.setNombre("Juan");

        Mockito.when(usuarioRepository.findByCorreo("test@correo.com"))
                .thenReturn(Optional.of(usuario));

        UsuarioDTO dto = usuarioService.obtenerUsuarioPorCorreo("test@correo.com");

        Assertions.assertEquals("Juan", dto.getNombre());
    }

    @Test
    void obtenerUsuarioPorCorreo_debeLanzarErrorSiNoExiste() {
        Mockito.when(usuarioRepository.findByCorreo("no@correo.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.obtenerUsuarioPorCorreo("no@correo.com")
        );
    }

    // ------------------------------------------------------------
    // actualizarUsuario()
    // ------------------------------------------------------------
    @Test
    void actualizarUsuario_debeActualizarCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setId(1);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Nuevo Nombre");

        Mockito.when(usuarioRepository.findByCorreo("test@correo.com"))
                .thenReturn(Optional.of(usuario));

        Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario);

        UsuarioDTO result = usuarioService.actualizarUsuario("test@correo.com", dto);

        Assertions.assertEquals("Nuevo Nombre", result.getNombre());
    }

    @Test
    void actualizarUsuario_debeLanzarErrorSiNoExiste() {
        Mockito.when(usuarioRepository.findByCorreo("notfound@correo.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.actualizarUsuario("notfound@correo.com", new UsuarioDTO())
        );
    }

    // ------------------------------------------------------------
    // existePorCorreo()
    // ------------------------------------------------------------
    @Test
    void existePorCorreo_true() {
        Mockito.when(usuarioRepository.findByCorreo("a@a.com"))
                .thenReturn(Optional.of(new Usuario()));

        Assertions.assertTrue(usuarioService.existePorCorreo("a@a.com"));
    }

    @Test
    void existePorCorreo_false() {
        Mockito.when(usuarioRepository.findByCorreo("a@a.com"))
                .thenReturn(Optional.empty());

        Assertions.assertFalse(usuarioService.existePorCorreo("a@a.com"));
    }
}