package com.bika.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String superAdminPassword = "superadmin123";
        
        // Generate new hashes
        String adminHash = encoder.encode(adminPassword);
        String superAdminHash = encoder.encode(superAdminPassword);
        
        System.out.println("=== NEW HASHES ===");
        System.out.println("Admin password: " + adminPassword);
        System.out.println("Admin hash: " + adminHash);
        System.out.println();
        System.out.println("SuperAdmin password: " + superAdminPassword);
        System.out.println("SuperAdmin hash: " + superAdminHash);
        
        // Test the hashes I provided
        System.out.println();
        System.out.println("=== TESTING PROVIDED HASHES ===");
        String providedAdminHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        String providedSuperAdminHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        
        System.out.println("Provided admin hash works: " + encoder.matches(adminPassword, providedAdminHash));
        System.out.println("Provided superadmin hash works: " + encoder.matches(superAdminPassword, providedSuperAdminHash));
        
        // Test the old hash from database
        System.out.println();
        System.out.println("=== TESTING OLD DATABASE HASH ===");
        String oldHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        System.out.println("Old hash works with admin123: " + encoder.matches(adminPassword, oldHash));
        System.out.println("Old hash works with superadmin123: " + encoder.matches(superAdminPassword, oldHash));
        
        // Test what password the old hash actually matches
        System.out.println();
        System.out.println("=== FINDING WHAT PASSWORD OLD HASH MATCHES ===");
        String[] testPasswords = {"admin123", "superadmin123", "password", "admin", "superadmin", "123456", "test"};
        for (String testPass : testPasswords) {
            if (encoder.matches(testPass, oldHash)) {
                System.out.println("Old hash matches password: " + testPass);
            }
        }
    }
} 