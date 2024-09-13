package com.carrot.carrotmarketclonecoding.config;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.FORBIDDEN;

import com.carrot.carrotmarketclonecoding.auth.exception.CustomAuthenticationEntryPoint;
import com.carrot.carrotmarketclonecoding.auth.filter.JwtAuthorizationFilter;
import com.carrot.carrotmarketclonecoding.auth.service.LogoutHandlerImpl;
import com.carrot.carrotmarketclonecoding.auth.service.LogoutSuccessHandlerImpl;
import com.carrot.carrotmarketclonecoding.auth.service.RefreshTokenRedisService;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.auth.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisService redisService;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthorizationFilter(authenticationManager, jwtUtil);
    }

    @Bean
    public LogoutHandlerImpl logoutHandlerService() {
        return new LogoutHandlerImpl(jwtUtil, redisService);
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandlerImpl();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
            .cors(cors -> cors.configurationSource(configurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandlerService())
                .logoutSuccessHandler(logoutSuccessHandler())
        );

        http.addFilterBefore(jwtAuthorizationFilter(authenticationManager(http)), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    ResponseUtil.fail(response, FORBIDDEN.getMessage(), HttpStatus.FORBIDDEN);
                }));

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/callback", "/hc", "/env").permitAll()
                .requestMatchers("/**").hasRole("USER")
                .anyRequest().permitAll()
        );

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);

        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
