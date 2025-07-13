package com.bika.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String token;
    private String email;
    private String role;
    private Long id;
    private String firstName;
    private String lastName;
    private Long companyId;
    private Long departmentId;
} 