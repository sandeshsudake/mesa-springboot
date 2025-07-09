package com.sandeshsudake.mesa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Keep BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class mySecurityConfig {
    @Autowired
    AdminDetailService adminDetailService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow public access to the root URL and common static assets
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/"),
                                AntPathRequestMatcher.antMatcher("/index.html"),
                                AntPathRequestMatcher.antMatcher("/favicon.ico"),
                                AntPathRequestMatcher.antMatcher("/assets/**"),
                                AntPathRequestMatcher.antMatcher("/webjars/**"),
                                AntPathRequestMatcher.antMatcher("/css/**"),
                                AntPathRequestMatcher.antMatcher("/js/**"),
                                AntPathRequestMatcher.antMatcher("/images/**"),
                                AntPathRequestMatcher.antMatcher("/home/**"), // Ensure /home/** is permitted
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/addFRF"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/registerEvents/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/addFRF"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/eventImg/**")
                        ).permitAll()
                        // Restrict /admin paths to users with the "ADMIN" role
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                        // All other requests not explicitly permitted or restricted require authentication
                        .anyRequest().authenticated()
                )
                // Configure form-based login
                .formLogin(formLogin -> formLogin
                        .defaultSuccessUrl("/logicsuccess/", true)
                        .permitAll()
                )
                // Configure logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/home/") // Redirect to /home/ after successful logout
                        .permitAll() // Allow access to the logout endpoint itself
                );

        return http.build();
    }

    @Bean
    PasswordEncoder getPasswordEncoder(){
        // Using BCryptPasswordEncoder for secure password hashing.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getPasswordEncoder());
        provider.setUserDetailsService(adminDetailService);
        return provider;
    }
}
