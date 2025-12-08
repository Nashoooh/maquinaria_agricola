package com.ignabasti.agricola.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // ----------------------------------------------------------------------
    // 1) MethodArgumentNotValidException
    // ----------------------------------------------------------------------
    @Test
    void handleValidationExceptions_debeRetornarErrores() {
        // Creamos un objeto con errores simulados
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "obj");

        bindingResult.addError(new FieldError("obj", "correo", "Correo inválido"));
        bindingResult.addError(new FieldError("obj", "nombre", "Nombre obligatorio"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Correo inválido", response.getBody().get("correo"));
        assertEquals("Nombre obligatorio", response.getBody().get("nombre"));
    }

    // ----------------------------------------------------------------------
    // 2) AuthenticationException
    // ----------------------------------------------------------------------
    @Test
    void handleAuthenticationException_debeRetornar401() {
        AuthenticationException ex = new AuthenticationException("Credenciales inválidas") {};

        ResponseEntity<Map<String, String>> response = handler.handleAuthenticationException(ex);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("No autorizado", response.getBody().get("error"));
        assertEquals("Credenciales inválidas", response.getBody().get("mensaje"));
    }

    // ----------------------------------------------------------------------
    // 3) AccessDeniedException
    // ----------------------------------------------------------------------
    @Test
    void handleAccessDeniedException_debeRetornar403() {
        AccessDeniedException ex = new AccessDeniedException("Sin permisos");

        ResponseEntity<Map<String, String>> response = handler.handleAccessDeniedException(ex);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Acceso denegado", response.getBody().get("error"));
        assertEquals("No tienes permisos para acceder a este recurso", response.getBody().get("mensaje"));
    }

    // ----------------------------------------------------------------------
    // 4) IllegalArgumentException (redirect + flash)
    // ----------------------------------------------------------------------
    @Test
    void handleIllegalArgumentException_debeRedirigirConFlash() {
        IllegalArgumentException ex = new IllegalArgumentException("ID inválido");
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = handler.handleIllegalArgumentException(ex, attrs);

        assertEquals("redirect:/home", result);
        assertTrue(attrs.getFlashAttributes().containsKey("error"));
        assertEquals("ID inválido", attrs.getFlashAttributes().get("error"));
    }

    // ----------------------------------------------------------------------
    // 5) Exception genérica (redirect + flash)
    // ----------------------------------------------------------------------
    @Test
    void handleGenericException_debeRedirigirConFlashGenerico() {
        Exception ex = new Exception("Fallo grave");
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = handler.handleGenericException(ex, attrs);

        assertEquals("redirect:/home", result);
        assertTrue(attrs.getFlashAttributes().containsKey("error"));
        assertEquals("Ha ocurrido un error inesperado", attrs.getFlashAttributes().get("error"));
    }
}