package com.rosewood.service;

import com.rosewood.dto.AuthResponse;
import com.rosewood.dto.LoginRequest;
import com.rosewood.dto.RegisterRequest;
import com.rosewood.enums.UserRole;
import com.rosewood.model.User;
import com.rosewood.repository.UserRepository;
import com.rosewood.security.JwtUtil;
import com.rosewood.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is deactivated. Contact admin.");
        }

        // Verify using original JavaFX SHA-256 + salt hashing
        boolean match = PasswordUtil.verifyPassword(req.getPassword(), user.getPasswordHash(), user.getSalt());
        if (!match) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getFullName(), user.getRole().name(), user.getUserId());
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.getRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException("Admin accounts cannot be created through public registration.");
        }
        if (userRepository.existsByUsername(req.getUsername().trim())) {
            throw new IllegalArgumentException("Username already taken.");
        }
        if (userRepository.existsByEmail(req.getEmail().trim())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(req.getPassword(), salt);

        User user = new User();
        user.setFullName(req.getFullName().trim());
        user.setUsername(req.getUsername().trim());
        user.setEmail(req.getEmail().trim());
        user.setPhone(req.getPhone().trim());
        user.setRole(req.getRole());
        user.setPasswordHash(hash);
        user.setSalt(salt);
        user.setActive(true);

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole().name());
        return new AuthResponse(token, saved.getUsername(), saved.getFullName(), saved.getRole().name(), saved.getUserId());
    }
}
