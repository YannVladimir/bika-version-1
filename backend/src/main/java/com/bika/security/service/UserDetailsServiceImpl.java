package com.bika.security.service;

import com.bika.user.entity.User;
import com.bika.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("UserDetailsService: Looking for user with identifier: '{}'", identifier);
        
        // First try to find by email (for login)
        Optional<User> userOptional = userRepository.findByEmail(identifier);
        
        // If not found by email, try to find by username (for JWT token validation)
        if (userOptional.isEmpty()) {
            log.debug("UserDetailsService: User not found by email, trying username: '{}'", identifier);
            userOptional = userRepository.findByUsername(identifier);
        }
        
        User user = userOptional.orElseThrow(() -> {
            log.error("UserDetailsService: User not found with identifier: '{}'", identifier);
            // Let's also check what users exist in the database
            var allUsers = userRepository.findAll();
            log.error("UserDetailsService: Total users in database: {}", allUsers.size());
            allUsers.forEach(u -> log.error("UserDetailsService: Found user - email: '{}', username: '{}', id: {}", 
                u.getEmail(), u.getUsername(), u.getId()));
            return new UsernameNotFoundException("User not found with identifier: " + identifier);
        });

        log.debug("UserDetailsService: Found user with email: '{}', username: '{}', id: {}", 
            user.getEmail(), user.getUsername(), user.getId());
        // Return the actual User entity which implements UserDetails
        return user;
    }
} 