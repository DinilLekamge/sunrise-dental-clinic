package com.sunrise.clinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Sunrise Dental Clinic system.
 *
 * Web pages use form-based login.
 * REST API endpoints use HTTP Basic authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security configuration for REST API endpoints.
     * HTTP Basic allows the API to be tested using tools such as Postman.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(requests -> requests
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Security configuration for the normal web application.
     * Unauthenticated users are redirected to the login page.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        .requestMatchers("/login").permitAll()

                        // Administration
                        .requestMatchers(
                                "/users/**",
                                "/dentists/**",
                                "/treatments/**"
                        ).hasRole("ADMIN")

                        // Patient management
                        .requestMatchers(
                                "/patients/new",
                                "/patients/save",
                                "/patients/*/edit"
                        ).hasRole("RECEPTIONIST")

                        .requestMatchers("/patients/**")
                        .hasAnyRole("RECEPTIONIST", "DENTIST")

                        // Appointment management
                        .requestMatchers(
                                "/appointments/new",
                                "/appointments/save",
                                "/appointments/*/edit",
                                "/appointments/*/cancel"
                        ).hasRole("RECEPTIONIST")

                        .requestMatchers("/appointments/**")
                        .hasAnyRole("RECEPTIONIST", "DENTIST")

                        // Billing
                        .requestMatchers("/billing/**")
                        .hasRole("RECEPTIONIST")

                        // Reports
                        .requestMatchers("/reports/**")
                        .hasAnyRole("ADMIN", "RECEPTIONIST")

                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                .exceptionHandling(handling ->
                        handling.accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}