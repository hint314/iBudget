package com.accounting.service;

import com.accounting.model.User;
import com.accounting.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalStateException("username exists");
        }
        
        // 密码规则校验
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("password too short");
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("password must contain letters and numbers");
        }

        User u = new User(username, encoder.encode(password));
        return userRepository.save(u);
    }

    public Optional<User> login(String username, String password) {
        Optional<User> u = findByUsername(username);
        if (u.isPresent() && encoder.matches(password, u.get().getPasswordHash())) {
            return u;
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    public void resetPassword(String username, String recoveryKey, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));
                
        if (!recoveryKey.equals(user.getRecoveryKey())) {
            throw new IllegalArgumentException("invalid_recovery_key");
        }
        
        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("password too short");
        }
        if (!newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("password must contain letters and numbers");
        }
        
        user.setPasswordHash(encoder.encode(newPassword));
        // Rotate recovery key after use for security
        user.setRecoveryKey(UUID.randomUUID().toString().substring(0, 8));
        userRepository.save(user);
    }
}
