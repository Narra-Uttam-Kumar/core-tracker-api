package com.core.tracker.controller;

import com.core.tracker.dto.Records.AuthRequest;
import com.core.tracker.model.User;
import com.core.tracker.repository.UserRepository;
import com.core.tracker.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthController(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtTokenUtil jwtTokenUtil,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username configuration occupied."));
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Operator initialized successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid structural identity credentials."));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }
}