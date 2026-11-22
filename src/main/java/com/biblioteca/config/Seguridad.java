package com.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Crea el objeto que cifra y compara contraseñas con BCrypt
@Configuration
public class Seguridad {
    @Bean
    public BCryptPasswordEncoder cifrador() {
        return new BCryptPasswordEncoder();
    }
}
