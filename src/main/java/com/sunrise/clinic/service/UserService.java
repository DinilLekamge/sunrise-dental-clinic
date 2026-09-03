package com.sunrise.clinic.service;

import com.sunrise.clinic.dto.UserForm;
import com.sunrise.clinic.entity.User;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Business layer for system users. Passwords are always stored as BCrypt hashes.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllByOrderByUsernameAsc();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id " + id));
    }

    public User save(UserForm form) {
        String username = form.getUsername().trim();
        Optional<User> existingWithSameName = userRepository.findByUsername(username);
        if (existingWithSameName.isPresent() && !existingWithSameName.get().getId().equals(form.getId())) {
            throw new BusinessRuleException("This username is already taken.");
        }

        User user;
        if (form.getId() == null) {
            if (form.getPassword() == null || form.getPassword().length() < 6) {
                throw new BusinessRuleException("A password of at least 6 characters is required for a new user.");
            }
            user = new User();
            user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        } else {
            user = findById(form.getId());
            if (form.getPassword() != null && !form.getPassword().isBlank()) {
                if (form.getPassword().length() < 6) {
                    throw new BusinessRuleException("A new password must be at least 6 characters long.");
                }
                user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
            }
        }

        user.setUsername(username);
        user.setRole(form.getRole());
        user.setActive(form.isActive());
        return userRepository.save(user);
    }

    public boolean toggleActive(Long id) {
        User user = findById(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
        return user.isActive();
    }

    /** The password is never copied back into the form. */
    @Transactional(readOnly = true)
    public UserForm toForm(Long id) {
        User user = findById(id);
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setRole(user.getRole());
        form.setActive(user.isActive());
        return form;
    }
}
