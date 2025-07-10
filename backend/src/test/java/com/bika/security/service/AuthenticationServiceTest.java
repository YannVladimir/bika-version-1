package com.bika.security.service;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.service.CompanyService;
import com.bika.security.dto.LoginRequest;
import com.bika.security.dto.LoginResponse;
import com.bika.security.dto.RegisterRequest;
import com.bika.security.dto.RegisterResponse;
import com.bika.user.entity.User;
import com.bika.user.entity.UserRole;
import com.bika.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CompanyService companyService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private CompanyDTO companyDTO;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .companyId(1L)
                .role(UserRole.USER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .active(true)
                .build();

        companyDTO = CompanyDTO.builder()
                .id(1L)
                .name("Test Company")
                .code("TEST")
                .email("company@test.com")
                .isActive(true)
                .build();
    }

    @Test
    void register_ShouldReturnToken_WhenValidRequest() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(companyService.findById(anyLong())).thenReturn(companyDTO);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        RegisterResponse response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        LoginResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
} 