package com.ignabasti.agricola.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JwtAuthorizationFilterTest {

    @InjectMocks
    private JwtAuthorizationFilter filter;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
        ReflectionTestUtils.setField(filter, "jwtService", jwtService);
    }

    @Test
    void rutaPublica_debeContinuarFiltro() throws Exception {
        when(request.getRequestURI()).thenReturn("/inicio");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void rutaPrivada_sinToken_debeResponder403() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).sendError(eq(403), anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValidoDesdeHeader_autenticaUsuario() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user@mail.com");
        when(jwtService.isTokenValid(anyString())).thenReturn(claims);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void tokenValidoDesdeCookie_autenticaUsuario() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(
            new Cookie[]{ new Cookie("jwt", "cookieToken") }
        );

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user@mail.com");
        when(jwtService.isTokenValid(anyString())).thenReturn(claims);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void tokenBase64_seDecodificaCorrectamente() throws Exception {
        String rawToken = "eyJtoken";
        String base64 = Base64.getEncoder().encodeToString(rawToken.getBytes());

        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + base64);
        when(jwtService.isTokenValid(rawToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("user@mail.com");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void tokenBase64Invalido_noRompeFlujo() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn("Bearer $$$");

        when(jwtService.isTokenValid("$$$")).thenThrow(new MalformedJwtException("bad"));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void jwtExpirado_responde403() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.isTokenValid("token"))
            .thenThrow(new ExpiredJwtException(null, null, "expirado"));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void getToken_headerNoBearer_debeIgnorar() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn("Basic abc");
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(eq(403), anyString());
    }

    @Test
    void getToken_headerBearerVacio() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn("Bearer   ");
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(eq(403), anyString());
    }

    @Test
    void getToken_sinCookies() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(eq(403), anyString());
    }

    @Test
    void getToken_cookiesVacias() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{});

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(eq(403), anyString());
    }

    @Test
    void getToken_cookieSinJwt() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(
            new Cookie[]{ new Cookie("otro", "123") }
        );

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(eq(403), anyString());
    }

    @Test
    void getToken_cookieJwtValida() throws Exception {
        when(request.getRequestURI()).thenReturn("/privada");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(
            new Cookie[]{ new Cookie("jwt", "token123") }
        );

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user@mail.com");
        when(jwtService.isTokenValid(anyString())).thenReturn(claims);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void rutaPublica_apiAuth_debePasarSinAutenticacion() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void rutaPublica_inicio() throws Exception {
        when(request.getRequestURI()).thenReturn("/inicio");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void rutaPublica_registro() throws Exception {
        when(request.getRequestURI()).thenReturn("/registro");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void rutaPublica_login() throws Exception {
        when(request.getRequestURI()).thenReturn("/login");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void rutaPublica_css() throws Exception {
        when(request.getRequestURI()).thenReturn("/css/styles.css");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void rutaPublica_js() throws Exception {
        when(request.getRequestURI()).thenReturn("/js/app.js");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void rutaPublica_img() throws Exception {
        when(request.getRequestURI()).thenReturn("/img/logo.png");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
