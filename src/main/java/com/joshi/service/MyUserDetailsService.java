package com.joshi.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (username.equals("admin")) {
            return User.withUsername("admin")
                    .password("{noop}admin123")
                    .roles("ADMIN")
                    .build();
        } else {
            return User.withUsername("user")
                    .password("{noop}user123")
                    .roles("USER")
                    .build();
        }
    }
}
