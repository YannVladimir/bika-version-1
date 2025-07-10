package com.bika.security.controller;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.service.CompanyService;
import com.bika.config.TestAuditorAwareConfig;
import com.bika.security.config.TestSecurityConfig;
import com.bika.security.dto.LoginRequest;
import com.bika.security.dto.RegisterRequest;
import com.bika.user.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import({TestSecurityConfig.class, TestAuditorAwareConfig.class})
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyService companyService;

    private CompanyDTO companyDTO;

    @BeforeEach
    void setUp() {
        // Create test company
        CompanyDTO newCompanyDTO = CompanyDTO.builder()
                .name("Test Company")
                .code("TEST")
                .email("company@test.com")
                .isActive(true)
                .build();
        companyDTO = companyService.createCompany(newCompanyDTO);
    }

    @Test
    void register_ShouldCreateNewUser_WhenValidRequest() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .companyId(companyDTO.getId())
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("invalid-email")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .companyId(companyDTO.getId())
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        // First registration
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .companyId(companyDTO.getId())
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Second registration with same email
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() throws Exception {
        // First register a user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .companyId(companyDTO.getId())
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Then try to login
        LoginRequest loginRequest = LoginRequest.builder()
                .email("testuser@example.com")
                .password("password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
} 