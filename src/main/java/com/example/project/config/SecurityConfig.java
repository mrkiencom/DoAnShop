package com.example.project.config;

import com.example.project.component.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        http
                .csrf(custumizer -> custumizer.disable())
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/register", "/login", "/about", "/images/**", "/", "/listCourses", "/courseDetail/**", "/search_courses", "/forgot-password", "reset-password", "send-email-reset-password").permitAll();
                    registry.anyRequest().authenticated(); // Đặt rule này ở cuối
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .successHandler(customAuthenticationSuccessHandler)// Đường dẫn đến trang login tùy chỉnh của bạn
                        .permitAll())
                .oauth2Login(oauth2Login -> {
                    oauth2Login.loginPage("/login")
                            .successHandler(((request, response, authentication) -> response.sendRedirect("/")));
                });

        //               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailService);
        return provider;
    }
}
