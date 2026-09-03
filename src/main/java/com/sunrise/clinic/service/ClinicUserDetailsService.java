package com.sunrise.clinic.service;

import com.sunrise.clinic.entity.User;
import com.sunrise.clinic.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Connects Spring Security to the users table. The stored value is a BCrypt
 * hash, so Spring Security compares the typed password against the hash.
 */
@Service
public class ClinicUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ClinicUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name())))
                .disabled(!user.isActive())
                .build();
    }
}
