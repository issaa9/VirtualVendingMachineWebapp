package com.example.finalyearproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //used for password encryption/hashing
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //disabled CSRF for development
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) //allow iframe for instructions page
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/guest-login").permitAll() //allow guest login for everyone
                        .requestMatchers("/register", "/login").permitAll() //allow login & register pages for everyone
                        .requestMatchers("/css/**", "/scripts/**", "/images/**", "/pdf/**", "/sounds/**").permitAll() //allow all static resources
                        .requestMatchers("/admin/**").hasRole("ADMIN") //restrict admin pages
                        .anyRequest().permitAll() //all other pages are accessible, restrictions are enforced in controllers
                )
                .formLogin(login -> login
                        .loginPage("/login") //use custom login page
                        .loginProcessingUrl("/login")
                        .successHandler(customLoginSuccessHandler)  //use the custom success handler class to handle a successful login
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/home"); //if non-admins try to access admin dashboard, redirect to home
                        })
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login") //reuse custom login page
                        .successHandler(customLoginSuccessHandler)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/log-out")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey") //secret key for hashing cookies
                        .tokenValiditySeconds(86400) //cookie lasts for 1 day (86400 seconds)
                        .userDetailsService(userDetailsService)
                );

        return http.build();
    }
}
