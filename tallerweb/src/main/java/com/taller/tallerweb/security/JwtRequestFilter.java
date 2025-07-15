package com.taller.tallerweb.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;  // Utilidad propia para manejo de JWT (extraer usuario, validar token, etc.)

    @Autowired
    private UserDetailsService userDetailsService;  // Servicio para cargar detalles del usuario (de la DB o memoria)

    /**
     * Este método se ejecuta para cada solicitud HTTP,
     * y aquí se filtra la petición para validar el JWT y setear el contexto de seguridad.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        System.out.println("=== JwtRequestFilter ===");
        System.out.println("Request URI: " + request.getRequestURI());

        if (request.getServletPath().startsWith("/api/auth")) {
            System.out.println("Ruta pública (auth), no requiere token.");
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JWT extraído: " + jwt);
            username = jwtUtil.extractUsername(jwt);
            System.out.println("Username extraído del token: " + username);
        } else {
            System.out.println("No se encontró token Bearer válido en el header.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("UserDetails cargados para: " + userDetails.getUsername());

            boolean tokenValido = jwtUtil.isTokenValid(jwt, userDetails.getUsername());
            System.out.println("¿Token válido? " + tokenValido);

            if (tokenValido) {
                Claims claims = jwtUtil.getAllClaimsFromToken(jwt);
                List<String> roles = claims.get("roles", List.class);
                System.out.println("Roles en token: " + roles);

                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("Autenticación seteada en SecurityContext.");
            } else {
                System.out.println("Token inválido o expirado.");
            }
        } else if (username == null) {
            System.out.println("No se extrajo username del token.");
        } else {
            System.out.println("Ya existe autenticación previa en el contexto.");
        }

        chain.doFilter(request, response);
    }
}
