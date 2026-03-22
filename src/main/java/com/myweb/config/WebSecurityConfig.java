package com.myweb.config;

import com.myweb.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 配置（JWT 无状态）
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173", "http://101.43.55.12"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 允许匿名访问上传后的静态资源，如背景图、音乐等
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        // 头像上传接口放行到控制器，由控制器内部检查登录状态
                        .requestMatchers(HttpMethod.POST, "/api/user/upload/avatar").permitAll()
                        // 投稿文章、申请友链需要登录
                        .requestMatchers(HttpMethod.POST, "/api/blog/submit").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/link/apply").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/profile/info").hasRole("ADMIN")
                        .requestMatchers("/api/auth/**", "/api/blog/**", "/api/comment/**",
                                "/api/link/**", "/api/profile/**", "/api/music/**").permitAll()
                        // 用户个人信息接口需登录
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
