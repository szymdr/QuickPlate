package com.quickplate.config;

import com.quickplate.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    JwtAuthenticationFilter jwtFilter) throws Exception {
        http
          .cors().and()
          .csrf().disable()
          .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .authorizeHttpRequests(auth -> auth
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers(
              "/api/auth/**", "/health", "/api/hello",
              "/v3/api-docs/**", "/api/docs/**", "/api/swagger-ui/**",
              "/api/restaurants/**",
              "/api/users/me"
            ).permitAll()
            .antMatchers(HttpMethod.POST, "/api/reservations").hasAnyRole("USER", "ADMIN")
            .antMatchers("/api/reservations/**", "/api/orders/**").hasAnyRole("USER", "ADMIN")
            .antMatchers("/api/users/**").hasRole("ADMIN")
            .anyRequest().authenticated()
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}