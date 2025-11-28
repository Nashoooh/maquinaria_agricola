package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.dto.LoginDTO;
import com.ignabasti.agricola.security.JwtService;
import com.ignabasti.agricola.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
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
    void setUp() { MockitoAnnotations.openMocks(this); }

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
}
