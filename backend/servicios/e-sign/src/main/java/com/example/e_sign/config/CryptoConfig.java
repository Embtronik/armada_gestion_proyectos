package com.example.e_sign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CryptoConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    // Usa {bcrypt} por defecto y soporta otros si algún día cambias el id
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // Si prefieres fijar bcrypt y control de costo:
    // return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(10);
  }
}
