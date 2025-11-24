package com.example.demo.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Permitir acceso público a login, registro y recursos estáticos
        if (path.equals("/login") || path.equals("/registro") 
            || path.startsWith("/css/") || path.startsWith("/js/") 
            || path.startsWith("/images/") || path.equals("/service-worker.js")
            || path.equals("/manifest.json") || path.equals("/api/auth/verify-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            response.sendRedirect("/login");
            return;
        }

        String token = header.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            // Obtener rol del token
            String role = (String) decodedToken.getClaims().getOrDefault("role", "USER");
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            // Crear autenticación para Spring Security
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, Collections.singleton(authority));

            SecurityContextHolder.getContext().setAuthentication(auth);
            request.setAttribute("firebaseUser", decodedToken);

            // Bloquear accesos a rutas sensibles si no es admin
            if (path.startsWith("/dashboard") || path.startsWith("/api/data/delete")) {
                if (!"ADMIN".equals(role)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("No tienes permisos para acceder a esta ruta.");
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.sendRedirect("/login");
        }
    }
}
