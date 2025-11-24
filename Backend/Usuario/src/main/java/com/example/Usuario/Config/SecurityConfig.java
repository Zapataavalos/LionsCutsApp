package com.example.Usuario.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {
        System.out.println("🔴🔴 ¡LA CONFIGURACIÓN DE SEGURIDAD SE ESTÁ CARGANDO! 🔴🔴");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                // AQUI ESTA EL CAMBIO: Agregué "/api/usuarios/**" a la lista.
                // Esto permite que el Carrito (y Postman) consulten usuarios sin token.
                .requestMatchers(
                    "/api/auth/**", 
                    "/api/usuarios/**",  // <--- ¡NUEVO! Acceso libre a usuarios
                    "/swagger-ui/**", 
                    "/swagger-ui.html", 
                    "/v3/api-docs/**"
                ).permitAll()
                
                // Cualquier otra petición requiere autenticación
                //.anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}