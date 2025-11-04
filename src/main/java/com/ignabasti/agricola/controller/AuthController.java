package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.UsuarioRepository;
import com.ignabasti.agricola.security.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // @PostMapping("/login")
    // public Map<String, Object> login(@RequestBody Map<String, String> request) {
    //     try {
    //         Authentication authentication = authenticationManager.authenticate(
    //             new UsernamePasswordAuthenticationToken(
    //                     request.get("correo"),
    //                     request.get("contrasena"))
    //         );
    //         log.info(authentication.getName());
    //         String token = jwtService.generateToken(authentication.getName());
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("token", token);
    //         response.put("usuario", authentication.getName());
    //         return response;
    //     } catch (AuthenticationException e) {
    //         e.printStackTrace();
    //         throw new RuntimeException("Credenciales inválidas");
    //     }
    // }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.get("correo"),
                    request.get("contrasena"))
            );

            log.info("Usuario autenticado: {}", authentication.getName());

            // Generar el token JWT
            String token = jwtService.generateToken(authentication.getName());

            // Crear cookie HttpOnly para el navegador
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true); // evita acceso desde JS
            cookie.setPath("/"); // accesible en toda la app
            cookie.setMaxAge(24 * 60 * 60); // 1 día
            response.addCookie(cookie);

            // Devolver también el token y usuario por JSON (para Postman)
            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("usuario", authentication.getName());

            return ResponseEntity.ok(body);

        } catch (AuthenticationException e) {
            log.error("Error de autenticación: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping(value = "/registro", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        // Validar si el correo ya existe
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        // Encriptar contraseña antes de guardar
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @GetMapping("/validarToken")
    public String getValidarToken(@RequestParam String token) {
        Claims claims = jwtService.isTokenValid(token);
        log.info("Validado: {}", claims.getSubject());
        return new String();
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Eliminar cookie JWT
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // mantener true si usas HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expira inmediatamente
        response.addCookie(cookie);

        return "redirect:/login"; // o a tu página de inicio de sesión
    }
    
}