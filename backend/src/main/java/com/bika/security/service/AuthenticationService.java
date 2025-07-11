package com.bika.security.service;

import com.bika.common.exception.BikaException;
import com.bika.common.exception.DuplicateResourceException;
import com.bika.company.dto.CompanyDTO;
import com.bika.company.service.CompanyService;
import com.bika.security.dto.LoginRequest;
import com.bika.security.dto.LoginResponse;
import com.bika.security.dto.RegisterRequest;
import com.bika.security.dto.RegisterResponse;
import com.bika.user.entity.User;
import com.bika.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CompanyService companyService;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        CompanyDTO company = companyService.findById(request.getCompanyId());
        if (company == null) {
            throw new BikaException("Company not found");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new RegisterResponse(token);
    }

    public LoginResponse login(LoginRequest request) {
        log.debug("AuthenticationService: Starting login process for email: {}", request.getEmail());
        
        try {
            log.debug("AuthenticationService: Attempting to authenticate with AuthenticationManager");
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            log.debug("AuthenticationService: Authentication successful, generating JWT token");
            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);
            
            log.debug("AuthenticationService: Login successful for user: {}", user.getEmail());
            return LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
            
        } catch (Exception e) {
            log.error("AuthenticationService: Error during login process", e);
            
            // Add debug logging to see what's happening
            try {
                User user = userRepository.findByEmail(request.getEmail()).orElse(null);
                if (user != null) {
                    log.debug("AuthenticationService: Found user in database - stored hash: {}", user.getPassword());
                    log.debug("AuthenticationService: Input password length: {}", request.getPassword().length());
                    log.debug("AuthenticationService: Input password: {}", request.getPassword());
                } else {
                    log.debug("AuthenticationService: No user found with email: {}", request.getEmail());
                }
            } catch (Exception ex) {
                log.debug("AuthenticationService: Error checking user details: {}", ex.getMessage());
            }
            
            throw e;
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
} 