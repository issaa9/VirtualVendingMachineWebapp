package com.example.finalyearproject.security;

import com.example.finalyearproject.model.User;
import com.example.finalyearproject.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private UserRepo userRepository;

    //method to load the user by their username/email
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrUsername(identifier, identifier)  //find by email OR username
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())  //correct username field
                .password(user.getPassword())  //hashed password from DB
                .roles(user.getRole())  //user role
                .build();
    }

}





