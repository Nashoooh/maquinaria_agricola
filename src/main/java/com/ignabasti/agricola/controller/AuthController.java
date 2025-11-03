package com.ignabasti.agricola.controller;

import com.ignabasti.agricola.model.Usuario;
import com.ignabasti.agricola.repository.UsuarioRepository;
import com.ignabasti.agricola.security.JwtService;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/obtenerToken")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.get("correo"),
                        request.get("contrasena"))
            );
            log.info(authentication.getName());
            String token = jwtService.generateToken(authentication.getName());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", authentication.getName());
            return response;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException("Credenciales inválidas");
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
    
}