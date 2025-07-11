package com.bika.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String superAdminPassword = "superadmin123";
        
        String adminHash = encoder.encode(adminPassword);
        String superAdminHash = encoder.encode(superAdminPassword);
        
        System.out.println("Admin password: " + adminPassword);
        System.out.println("Admin hash: " + adminHash);
        System.out.println();
        System.out.println("SuperAdmin password: " + superAdminPassword);
        System.out.println("SuperAdmin hash: " + superAdminHash);
        
        // Verify the hashes
        System.out.println();
        System.out.println("Verifying admin hash: " + encoder.matches(adminPassword, adminHash));
        System.out.println("Verifying superadmin hash: " + encoder.matches(superAdminPassword, superAdminHash));
    }
} 