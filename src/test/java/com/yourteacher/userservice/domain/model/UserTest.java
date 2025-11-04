package com.yourteacher.userservice.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad User del dominio
 */
class UserTest {
    
    @Test
    void shouldValidateCorrectEmail() {
        User user = User.builder()
                .email("test@example.com")
                .build();
        
        assertTrue(user.hasValidEmail());
    }
    
    @Test
    void shouldInvalidateIncorrectEmail() {
        User user = User.builder()
                .email("invalid-email")
                .build();
        
        assertFalse(user.hasValidEmail());
    }
    
    @Test
    void shouldReturnFullName() {
        User user = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .build();
        
        assertEquals("Juan Pérez", user.getFullName());
    }
    
    @Test
    void shouldActivateUser() {
        User user = User.builder()
                .status(UserStatus.INACTIVE)
                .build();
        
        user.activate();
        
        assertTrue(user.isActive());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }
    
    @Test
    void shouldDeactivateUser() {
        User user = User.builder()
                .status(UserStatus.ACTIVE)
                .build();
        
        user.deactivate();
        
        assertFalse(user.isActive());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }
    
    @Test
    void shouldCheckUserHasRole() {
        User user = User.builder()
                .roles(Set.of(Role.STUDENT))
                .build();

        assertTrue(user.hasRole(Role.STUDENT));
        assertFalse(user.hasRole(Role.ADMIN));
    }
}
