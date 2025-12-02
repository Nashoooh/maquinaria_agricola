package com.ignabasti.agricola.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService realJwtService;

    @Test
    void accesoSinToken_debeRetornar403() throws Exception {
        mockMvc.perform(get("/maquinaria/detalle?id=1"))
            .andExpect(status().isForbidden());
    }

    @Test
    void accesoPublico_debeRetornar200() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk());
    }

    // Para este test, se necesita un JWT válido generado por JwtService
    @Test
    void accesoConTokenValido_debeRetornar200() throws Exception {
        // Genera un JWT válido para el usuario de prueba
        String jwt = realJwtService.generateToken("test@mail.com");
        mockMvc.perform(get("/maquinaria/detalle?id=1")
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk());
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String username = "testuser";
        String token = realJwtService.generateToken(username);

        // Act
        String extracted = realJwtService.extractUsername(token);

        // Assert
        assertEquals(username, extracted);
    }
}
