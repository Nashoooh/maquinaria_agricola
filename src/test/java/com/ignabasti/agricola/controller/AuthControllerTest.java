package com.ignabasti.agricola.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ignabasti.agricola.dto.LoginDTO;
import com.ignabasti.agricola.dto.UsuarioDTO;
import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.security.JwtService;
import com.ignabasti.agricola.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() { 
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_exitoRetornaTokenYCookie() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreo("test@mail.com");
        loginDTO.setContrasena("1234");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@mail.com");
        when(jwtService.generateToken(anyString())).thenReturn("token123");

        ResponseEntity<Map<String, Object>> result = authController.login(loginDTO, response);

        assertEquals(200, result.getStatusCode().value());

    }

    @Test
    void login_errorRetorna401() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreo("fail@mail.com");
        loginDTO.setContrasena("bad");
        when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("fail") {});

        ResponseEntity<Map<String, Object>> result = authController.login(loginDTO, response);

        assertEquals(401, result.getStatusCode().value());
    }

    // Cobertura restante
    @Test
    void login_exitoso() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreo("test@mail.com");
        loginDTO.setContrasena("1234");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@mail.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken("test@mail.com")).thenReturn("TOKEN_FAKE");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("TOKEN_FAKE"))
                .andExpect(jsonPath("$.usuario").value("test@mail.com"));

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken("test@mail.com");
    }

    @Test
    void login_fallaPorCredenciales() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreo("fail@mail.com");
        loginDTO.setContrasena("1234");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\":\"test@mail.com\",\"contrasena\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    void registrarUsuario_exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Pedro");
        usuario.setCorreo("nuevo@mail.com");

        when(usuarioService.existePorCorreo("nuevo@mail.com")).thenReturn(false);
        when(usuarioService.registrarUsuario(any(UsuarioDTO.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/auth/registro")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":\"Pedro\",\"correo\":\"nuevo@mail.com\",\"contrasena\":\"123456\"}"))
            .andExpect(status().isOk())
            .andExpect(content().string("Usuario registrado exitosamente"));
    }

    @Test
    void registrarUsuario_correoDuplicado() throws Exception {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setCorreo("existente@mail.com");

    when(usuarioService.existePorCorreo("test@mail.com")).thenReturn(true);

    mockMvc.perform(post("/api/auth/registro")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":\"Pedro\",\"correo\":\"test@mail.com\",\"contrasena\":\"123456\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("El correo ya está registrado"));
    }

    @Test
    void validarToken() {
        String token = "TOKEN_FAKE";
        when(jwtService.isTokenValid(token)).thenReturn(Mockito.mock(io.jsonwebtoken.Claims.class));

        authController.getValidarToken(token);

        verify(jwtService).isTokenValid(token);
    }

    @Test
    void logout_eliminaCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("redirect:/login"));
    }
}
