package com.sandeshsudake.mesa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod for specific HTTP method matching
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Import AntPathRequestMatcher for flexible path matching

@Configuration
@EnableWebSecurity
public class mySecurityConfig {
    @Autowired
    AdminDetailService adminDetailService; // This will now be correctly injected due to @Service on AdminDetailService

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow public access to the root URL and common static assets
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/"), // Access to the welcome page (index.html)
                                AntPathRequestMatcher.antMatcher("/index.html"), // Direct access to index.html if needed
                                AntPathRequestMatcher.antMatcher("/favicon.ico"), // Favicon
                                AntPathRequestMatcher.antMatcher("/assets/**"), // Your /assets folder for CSS/JS
                                AntPathRequestMatcher.antMatcher("/webjars/**"), // If you use WebJars for frontend libraries
                                AntPathRequestMatcher.antMatcher("/css/**"), // Other common static folders
                                AntPathRequestMatcher.antMatcher("/js/**"),
                                AntPathRequestMatcher.antMatcher("/images/**"), // For images directly under /images/
                                AntPathRequestMatcher.antMatcher("/home/**"), // Critical: Permits access to static files under /home/ (e.g., images like "Precision-Cam-Shafts-Solapur2.jpg")
                                // Permit specific public form submission endpoints (POST requests)
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/addFRF"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/registerEvents/**"),
                                // Permit GET requests to specific public endpoints (e.g., dynamic image serving)
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/addFRF"), // Permit GET to the FRF form page itself
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/eventImg/**") // For dynamic event images (QR codes)
                        ).permitAll() // All matched paths above are publicly accessible
                        // Restrict /admin paths to users with the "ADMIN" role
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                        // All other requests not explicitly permitted or restricted require authentication
                        .anyRequest().authenticated()
                )
                // Configure form-based login
                .formLogin(formLogin -> formLogin
                        .defaultSuccessUrl("/logicsuccess/", true) // Redirect after successful login
                        .permitAll() // Allow access to the login page itself (/login)
                )
                // Configure logout with default settings (requires POST with CSRF token by default)
                .logout(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    PasswordEncoder getPasswordEncoder(){
        // WARNING: NoOpPasswordEncoder is INSECURE for production environments.
        // It means passwords are not hashed. For production, you MUST use a strong password encoder like BCryptPasswordEncoder:
        // return new BCryptPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getPasswordEncoder());
        provider.setUserDetailsService(adminDetailService); // Inject the service that loads user details
        return provider;
    }
}
