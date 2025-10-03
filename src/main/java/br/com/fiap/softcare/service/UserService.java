package br.com.fiap.softcare.service;

import br.com.fiap.softcare.enums.UserRole;
import br.com.fiap.softcare.model.AuditLog;
import br.com.fiap.softcare.model.User;
import br.com.fiap.softcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for User operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    
    /**
     * Create a new user
     */
    public User createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso: " + user.getEmail());
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(UserRole.EMPLOYEE);
        }
        
        User savedUser = userRepository.save(user);
        
        // Log the creation
        auditLogService.logUserCreated(savedUser);
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Update user
     */
    public User updateUser(String id, User updatedUser) {
        log.info("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        
        // Update allowed fields
        existingUser.setName(updatedUser.getName());
        
        // Check email uniqueness if changed
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email já está em uso: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        
        // Update role if provided and user has permission
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }
        
        User savedUser = userRepository.save(existingUser);
        
        // Log the update
        auditLogService.logUserUpdated(savedUser);
        
        log.info("User updated successfully: {}", savedUser.getId());
        return savedUser;
    }
    
    /**
     * Delete user by ID
     */
    public void deleteUser(String id) {
        log.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        
        userRepository.deleteById(id);
        
        // Log the deletion
        auditLogService.logEvent("USER_DELETED", 
                "User deleted: " + user.getEmail(), user.getId(), true);
        
        log.info("User deleted successfully: {}", id);
    }
    
    /**
     * Find all users
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Find users by role
     */
    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Validate user credentials for login
     */
    public boolean validateCredentials(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            boolean isValid = passwordEncoder.matches(password, userOpt.get().getPassword());
            
            if (isValid) {
                auditLogService.logUserLogin(userOpt.get().getId(), email);
            } else {
                auditLogService.logEvent(AuditLog.EventTypes.USER_LOGIN, 
                        "Failed login attempt for email: " + email, null, false);
            }
            
            return isValid;
        }
        
        auditLogService.logEvent(AuditLog.EventTypes.USER_LOGIN, 
                "Failed login attempt for non-existent email: " + email, null, false);
        return false;
    }
    
    /**
     * Change user password
     */
    public void changePassword(String userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));
        
        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Log password change
        auditLogService.logEvent("PASSWORD_CHANGED", 
                "Password changed for user: " + user.getEmail(), userId, true);
        
        log.info("Password changed successfully for user: {}", userId);
    }
}