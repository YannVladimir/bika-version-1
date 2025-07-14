package com.bika.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    
    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Company code is required")
    @Size(max = 50, message = "Company code must not exceed 50 characters")
    private String code;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;
    
    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private boolean isActive;
} 