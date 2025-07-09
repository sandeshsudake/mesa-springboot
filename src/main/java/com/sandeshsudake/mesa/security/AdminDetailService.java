package com.sandeshsudake.mesa.security;

import com.sandeshsudake.mesa.entity.admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.sandeshsudake.mesa.repository.adminRepo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AdminDetailService implements UserDetailsService {

    private final adminRepo adminRepo;

    public AdminDetailService(adminRepo adminRepo){
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        admin admin = adminRepo.findByadminUserName(username);

        if (admin == null){
            // FIX: Use the 'username' variable here for a clear message
            throw new UsernameNotFoundException("No admin found with username: " + username);
        }

        return admin; // Correctly returns the admin object as UserDetails
    }
}