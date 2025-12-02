// Archivo: com.example.demo.config.FirebaseTokenFilter.java

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

        // 1. Obtener el token del encabezado Authorization
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            // No hay token o el formato es incorrecto, continuar sin autenticar
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el JWT (ignorar "Bearer ")
        String authToken = header.substring(7);

        try {
            // 3. Validar el token con Firebase Admin SDK
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(authToken);
            
            // 4. Crear un objeto de autenticación de Spring Security
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    decodedToken.getUid(), 
                    null, // Credenciales nulas
                    Collections.emptyList() 
                );
            
            // 5. Establecer detalles y colocar en el contexto de seguridad
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (FirebaseAuthException e) {
            // Token inválido (esto causará un 403/401 más adelante si la ruta es protegida)
            System.err.println("Firebase Token Invalido: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}