package com.bika.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class FindPassword {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // The hash from the database
        String databaseHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        
        // Common passwords to test
        String[] commonPasswords = {
            "password", "admin", "admin123", "superadmin", "superadmin123", 
            "123456", "test", "user", "root", "secret", "pass", "admin1234",
            "password123", "admin@123", "superadmin@123", "bika", "bika123"
        };
        
        System.out.println("Testing hash: " + databaseHash);
        System.out.println("=====================================");
        
        for (String testPassword : commonPasswords) {
            if (encoder.matches(testPassword, databaseHash)) {
                System.out.println("MATCH FOUND! Password: " + testPassword);
                break;
            }
        }
        
        // If no match found, generate a new hash for admin123
        System.out.println("\nGenerating new hash for admin123:");
        String newHash = encoder.encode("admin123");
        System.out.println("New hash for admin123: " + newHash);
        System.out.println("Verification: " + encoder.matches("admin123", newHash));
    }
} 