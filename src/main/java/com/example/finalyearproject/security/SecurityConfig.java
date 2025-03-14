package com.example.finalyearproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //used for password encryption/hashing
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //disable CSRF for development (enable later in production)
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll()  //allow access to all pages for development
//                        .requestMatchers("/register", "/login").permitAll() //allow login & register pages for everyone
//                        .requestMatchers("/css/**", "/scripts/**", "/images/**", "/pdf/**").permitAll() //allow all static resources
//                        .requestMatchers("/admin/**").hasRole("ADMIN") //restrict admin pages for later
//                        .anyRequest().authenticated() //all other pages require to be logged in first to access
                )
                .formLogin(login -> login
                        .loginPage("/login") //use custom login page
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true) //redirect to home page after login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey") //secret key for hashing cookies
                        .tokenValiditySeconds(86400) //lasts for 1 day (86400 seconds)
                        .userDetailsService(userDetailsService)
                );

        return http.build();
    }
}
