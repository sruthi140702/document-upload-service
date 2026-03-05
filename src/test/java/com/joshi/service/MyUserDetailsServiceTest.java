package com.joshi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @InjectMocks
    private  MyUserDetailsService service;

    @Test
    void testLoadUserByUsername_Admin() {

        UserDetails userDetails = service.loadUserByUsername("admin");

        assertEquals("admin", userDetails.getUsername());
        assertEquals("{noop}admin123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsername_User() {

        UserDetails userDetails = service.loadUserByUsername("user");


        assertEquals("user", userDetails.getUsername());
        assertEquals("{noop}user123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
    @Test
    void testLoadUserByUsername_EmptyString() {

        UserDetails userDetails = service.loadUserByUsername("");


        assertEquals("user", userDetails.getUsername());
        assertEquals("{noop}user123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}
