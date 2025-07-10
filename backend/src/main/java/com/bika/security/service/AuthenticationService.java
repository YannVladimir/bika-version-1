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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
} 