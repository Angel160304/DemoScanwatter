// Archivo: com.example.demo.config.FirebaseTokenFilter.java (COMPLETO Y CORREGIDO)

package com.example.demo.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenFilter(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            // Si no hay token, continúa.
            filterChain.doFilter(request, response);
            return;
        }

        String authToken = header.substring(7);

        try {
            // 1. Validar el token con Firebase Admin SDK
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(authToken);

            // 2. Autenticación exitosa: Establecer contexto de seguridad
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            decodedToken.getUid(),
                            null,
                            Collections.emptyList()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (FirebaseAuthException e) {
            // 3. Token Inválido: Devolver explícitamente 401 Unauthorized
            System.err.println("Firebase Token Invalido: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token de Firebase Inválido");
            return; // Detiene ejecución
        }

        filterChain.doFilter(request, response);
    } // <<-- cierre método
} // <<-- cierre clase
