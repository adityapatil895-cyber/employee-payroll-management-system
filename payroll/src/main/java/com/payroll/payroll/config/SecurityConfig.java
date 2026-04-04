package com.payroll.payroll.config;

import com.payroll.payroll.service.CustomUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()

                        // Admin dashboard only
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // HR dashboard only
                        .requestMatchers("/hr/dashboard").hasAuthority("ROLE_HR")

                        // HR attendance routes → both Admin and HR
                        .requestMatchers("/hr/attendance/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")

                        // Employee only
                        .requestMatchers("/employee/**").hasAuthority("ROLE_EMPLOYEE")

                        // Employees management → Admin only
                        .requestMatchers("/employees/**").hasAuthority("ROLE_ADMIN")

                        // Payroll → Admin and HR
                        .requestMatchers("/payroll/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")

                        // Attendance → Admin and HR
                        .requestMatchers("/attendance/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")

                        // Everything else → just be logged in
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
            } else if (role.equals("ROLE_HR")) {
                response.sendRedirect("/hr/dashboard");
            } else {
                response.sendRedirect("/employee/dashboard");
            }
        };
    }
}