package com.bika.security.service;

import com.bika.user.entity.User;
import com.bika.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("UserDetailsService: Looking for user with email: '{}'", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("UserDetailsService: User not found with email: '{}'", email);
                    // Let's also check what users exist in the database
                    var allUsers = userRepository.findAll();
                    log.error("UserDetailsService: Total users in database: {}", allUsers.size());
                    allUsers.forEach(u -> log.error("UserDetailsService: Found user - email: '{}', id: {}", u.getEmail(), u.getId()));
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.debug("UserDetailsService: Found user with email: '{}', id: {}", user.getEmail(), user.getId());
        // Return the actual User entity which implements UserDetails
        return user;
    }
} 