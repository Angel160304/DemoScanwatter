package com.example.demo.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
            || path.startsWith("/images/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // Bloquear si no hay token o es incorrecto
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendRedirect("/login"); // Redirige al login
            return;
        }

        String token = header.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            // Crear autenticación para Spring Security
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, null);

            SecurityContextHolder.getContext().setAuthentication(auth);
            request.setAttribute("firebaseUser", decodedToken);

            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Token inválido → redirige al login
            response.sendRedirect("/login");
        }
    }
}
