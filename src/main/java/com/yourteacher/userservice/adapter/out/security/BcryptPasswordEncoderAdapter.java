package com.yourteacher.userservice.adapter.out.security;

import com.yourteacher.userservice.domain.port.out.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador para encriptación de contraseñas usando BCrypt
 * Implementa el puerto PasswordEncoder del dominio
 */
@Component
@RequiredArgsConstructor
public class BcryptPasswordEncoderAdapter implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Override
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
