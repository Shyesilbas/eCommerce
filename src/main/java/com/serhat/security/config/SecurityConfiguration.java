package com.serhat.security.config;

import com.serhat.security.component.CustomAccessDeniedHandler;
import com.serhat.security.jwt.JwtAuthenticationFilter;
import com.serhat.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFiler;
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    corsConfig.setMaxAge(3600L);
                    return corsConfig;
                }))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/auth/login","/auth/register","/auth/logout","/api/products/info/id/{productCode}","/api/products/info/{productCode}","/api/products/allProducts","/api/products/categories").permitAll()
                        .requestMatchers("/api/products/byCategory").permitAll()
                        .requestMatchers("/api/products/totalCount").permitAll()
                        .requestMatchers("/user/forgot-password").permitAll()
                        .requestMatchers("/price-history/**").permitAll()
                        .requestMatchers("/api/products/totalCountByCategory").permitAll()
                        .requestMatchers("/api/products/byBrand").permitAll()
                        .requestMatchers("/api/products/most-sellers").permitAll()
                        .requestMatchers("/api/products/most-sellers/category").permitAll()
                        .requestMatchers("/api/products/byPriceAndCategory").permitAll()
                        .requestMatchers("/api/products/byPriceRange").permitAll()
                        .requestMatchers("/api/products/allProductsWithoutPagination").permitAll()
                        .requestMatchers("/comment/comments-by-product").permitAll()
                        .requestMatchers("/comment/least-helpful-comments").permitAll()
                        .requestMatchers("/comment/most-helpful-comments").permitAll()
                        .requestMatchers("/comment/products/average-rating").permitAll()
                        .requestMatchers("/comment/average-rating-for-brand").permitAll()
                        .requestMatchers("/auth/test/CUSTOMER").hasRole("CUSTOMER")
                        .requestMatchers("/api/products/addProduct").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFiler,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(8);
    }


}
