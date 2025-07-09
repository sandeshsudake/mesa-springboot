package com.sandeshsudake.mesa.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Required for getAuthorities()
import org.springframework.security.core.userdetails.UserDetails; // Required to implement this interface

import java.util.Collection; // Use Collection as return type for getAuthorities()
import java.util.HashSet;
import java.util.Set;

@Document
@Data
@AllArgsConstructor // Provided by you
@NoArgsConstructor // Provided by you
public class admin implements UserDetails { // <--- **Crucial Change: Implement UserDetails**

    @Id
    private String adminId; // It's good practice to make fields private
    private String adminUserName;
    private String adminEmail;
    private String role = "ROLE_ADMIN"; // Default role
    private String adminPassword;

    // **IMPORTANT:** This constructor is necessary because AdminDetailService is calling it.
    // It's effectively creating a UserDetails object from the admin data retrieved from DB.
    public admin(String adminUserName, String adminPassword, Set<GrantedAuthority> authorities) {
        this.adminUserName = adminUserName;
        this.adminPassword = adminPassword;
        // The 'authorities' passed here are not directly stored as a field in 'admin'
        // because 'getAuthorities()' below generates them based on 'this.role'.
        // If you had multiple dynamic roles per admin, you'd need a Set<GrantedAuthority> field.
    }

    // --- UserDetails Interface Methods - These *MUST* be implemented ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(this.role)); // Use the 'role' field from this admin object
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.adminPassword; // Return the password stored in this admin object
    }

    @Override
    public String getUsername() {
        return this.adminUserName; // Return the username stored in this admin object
    }

    // --- Account Status Methods (return true by default, implement logic if needed) ---
    @Override
    public boolean isAccountNonExpired() {
        return true; // Return false if account has expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Return false if account is locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Return false if password has expired
    }

    @Override
    public boolean isEnabled() {
        return true; // Return false if account is disabled
    }
}
