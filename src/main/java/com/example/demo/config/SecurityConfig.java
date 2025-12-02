// Archivo: com.example.demo.config.SecurityConfig.java

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

// Importaciones necesarias para la configuración de seguridad
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Nuevo
import org.springframework.beans.factory.annotation.Autowired; 

// Importación para el filtro JWT (asumimos que existe)
// import com.example.demo.security.FirebaseTokenFilter; 


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	// Debes inyectar el filtro aquí si ya lo creaste:
	// @Autowired
	// private FirebaseTokenFilter firebaseTokenFilter; 

	/**
	 * Define el SecurityFilterChain: Reglas de autorización y filtros de seguridad.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
			// 1. Desactivar CSRF
			.csrf(AbstractHttpConfigurer::disable) 
			
			// 2. Aplicar la configuración de CORS
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			
			// 🛑 CRÍTICO: Asegurar que el servidor es completamente Stateless (sin sesiones HTTP)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
			)
			
			// 3. Definir reglas de autorización 
			.authorizeHttpRequests(auth -> auth
                // 🛑 RUTAS PÚBLICAS Y ESTATICAS: EXCLUSIÓN ROBUSTA
                .requestMatchers(
                    "/api/login/**", 
                    "/api/registro/**", // Permitir endpoint de registro (si lo tienes)
                    "/login.html", 
                    "/registro.html",   // ⬅️ CRÍTICO: ¡Ahora incluído!
                    "/index.html",      // CRÍTICO: La página de inicio
                    "/favicon.ico", 
                    "/manifest.json",
                    "/images/**",       // Cualquier imagen
                    "/css/**",          // Todos los archivos CSS
                    "/js/**",           // Todos los archivos JS
                    "/*.css",           // CSS en la raíz
                    "/*.js",            // JS en la raíz (como auth.js)
                    "/*.html"           // HTML en la raíz
                ).permitAll()
                
				// Requerir autenticación (via el filtro JWT) para cualquier otra solicitud
				.anyRequest().authenticated()
			)
			// Deshabilitar login basado en formulario y autenticación básica
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		// 🛑 Paso pendiente: Añadir el filtro de autenticación JWT
		// Este filtro se ejecuta ANTES que la autenticación de nombre/contraseña.
        // http.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
		return http.build();
	}


	/**
	 * Define la configuración de CORS abierta para Render.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		
		// Permitir credenciales (necesario para el token JWT)
		config.setAllowCredentials(true); 
		
		// Permitir todos los orígenes
		config.addAllowedOriginPattern("*"); 
		
		// Permitir todos los métodos y cabeceras (incluyendo 'Authorization')
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");
		
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}