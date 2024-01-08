package com.xpresPayment.Airtimeservice.configs;

import com.xpresPayment.Airtimeservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigFilter {
    private final LogoutHandler logoutHandler;
    private final JwtAuthenticationFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    private final String[] WHITE_LIST = new String[]{
            "/api/v1/user/auth/**",
            "/api/v1/user/airtime/all",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable) //cross site request forgery is disabled for security reasons not to allow clone requests
                .authorizeHttpRequests((authorize)-> authorize.requestMatchers(WHITE_LIST)
                        .permitAll() // Allows access to endpoints in the "WHITE_LIST" to access with authentication
                        .anyRequest()
                        .authenticated()) // Any access to any endpoint that is not on the "WHITE_LIST" must be authenticated
                .sessionManagement((session)-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //User info will not be stored in a session
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) //Every request to an endpoint that is not in the "WHITE_LIST" must pass through the JWT Filter for authentication.
                .logout((logout)->logout.logoutUrl("/api/v1/user/logout")
                        .addLogoutHandler(logoutHandler) //This handles logout and clear SecurityContextHolder once the endpoint "/api/v1/user/logout" is called
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
                .build();
    }

    //This defines the inbound rule that is the origin allowed to get resources from you server
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
