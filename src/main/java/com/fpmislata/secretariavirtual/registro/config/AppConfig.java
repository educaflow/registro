package com.fpmislata.secretariavirtual.registro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Value("${app.centro.code}")
    private String centroCode;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new ShiroArgon2PasswordEncoder();
    }

    public String getCentroCode() {
        return centroCode;
    }
}
