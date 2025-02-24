package com.example.finalyearproject.security;

import com.example.finalyearproject.model.User;
import com.example.finalyearproject.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())  // ✅ Correct username field
                .password(user.getPassword())  // ✅ Hashed password from DB (Spring Security will compare it)
                .roles(user.getRole())  // ✅ Assign roles
                .build();
    }
}



