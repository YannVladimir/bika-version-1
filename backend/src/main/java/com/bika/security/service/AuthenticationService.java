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
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        return RegisterResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .companyId(savedUser.getCompany() != null ? savedUser.getCompany().getId() : null)
                .departmentId(savedUser.getDepartment() != null ? savedUser.getDepartment().getId() : null)
                .build();
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
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .build();
            
        } catch (Exception e) {
            log.error("AuthenticationService: Error during login for email: {}", request.getEmail(), e);
            throw new RuntimeException("Authentication failed", e);
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
} 