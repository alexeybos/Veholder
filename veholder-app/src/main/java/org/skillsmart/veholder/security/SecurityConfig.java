package org.skillsmart.veholder.security;

import org.skillsmart.veholder.service.ManagerDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    private final ManagerDetailsService managerDetailsService;

    public SecurityConfig(ManagerDetailsService managerDetailsService) {
        this.managerDetailsService = managerDetailsService;
    }

    //@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers("/api/**").hasRole("E1")
                        .anyRequest().authenticated())
                .userDetailsService(managerDetailsService)
                .formLogin(withDefaults());
        return http.build();
    }

    //@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
