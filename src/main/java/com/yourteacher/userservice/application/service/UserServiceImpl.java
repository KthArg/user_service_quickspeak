package com.yourteacher.userservice.application.service;

import com.yourteacher.userservice.domain.model.User;
import com.yourteacher.userservice.domain.model.UserStatus;
import com.yourteacher.userservice.domain.port.in.UserService;
import com.yourteacher.userservice.domain.port.out.PasswordEncoder;
import com.yourteacher.userservice.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de usuarios (Application Layer)
 * Contiene la lógica de aplicación y orquesta los casos de uso
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User registerUser(User user) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Validar email
        if (!user.hasValidEmail()) {
            throw new IllegalArgumentException("El email no es válido");
        }
        
        // Encriptar contraseña
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        // Generar avatarSeed único para el usuario
        String avatarSeed = generateAvatarSeed(user.getEmail());

        // Crear usuario con valores iniciales
        User newUser = User.builder()
                .email(user.getEmail())
                .password(encodedPassword)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarSeed(avatarSeed)
                .roles(user.getRoles())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(newUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Actualizar solo campos permitidos
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .email(existingUser.getEmail()) // Email no se puede cambiar
                .password(existingUser.getPassword()) // Password se cambia por otro endpoint
                .firstName(user.getFirstName() != null ? user.getFirstName() : existingUser.getFirstName())
                .lastName(user.getLastName() != null ? user.getLastName() : existingUser.getLastName())
                .avatarSeed(existingUser.getAvatarSeed()) // AvatarSeed no se puede cambiar
                .roles(user.getRoles() != null ? user.getRoles() : existingUser.getRoles())
                .status(existingUser.getStatus())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(updatedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        user.activate();

        User activatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarSeed(user.getAvatarSeed())
                .roles(user.getRoles())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(activatedUser);
    }
    
    @Override
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        user.deactivate();

        User deactivatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarSeed(user.getAvatarSeed())
                .roles(user.getRoles())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(deactivatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateCredentials(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty() || !user.get().isActive()) {
            return false;
        }

        return passwordEncoder.matches(password, user.get().getPassword());
    }

    @Override
    public User changePassword(Long id, String currentPassword, String newPassword) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Encriptar la nueva contraseña
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Actualizar el usuario con la nueva contraseña
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .email(existingUser.getEmail())
                .password(encodedPassword)
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .avatarSeed(existingUser.getAvatarSeed())
                .roles(existingUser.getRoles())
                .status(existingUser.getStatus())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(updatedUser);
    }

    @Override
    public User changeEmail(Long id, String newEmail) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar que el nuevo email no esté en uso
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar formato del email
        if (newEmail == null || !newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El email no es válido");
        }

        // Actualizar el usuario con el nuevo email
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .email(newEmail)
                .password(existingUser.getPassword())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .avatarSeed(existingUser.getAvatarSeed())
                .roles(existingUser.getRoles())
                .status(existingUser.getStatus())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(updatedUser);
    }

    /**
     * Genera un avatarSeed único basado en el email del usuario
     * Este seed puede ser usado con servicios como DiceBear para generar avatares
     */
    private String generateAvatarSeed(String email) {
        // Usamos UUID aleatorio para generar un seed único y reproducible
        // Alternativamente se podría usar el email directamente o un hash del mismo
        return UUID.randomUUID().toString();
    }
}
