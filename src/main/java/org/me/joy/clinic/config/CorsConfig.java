package org.me.joy.clinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * CORS跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源模式 (解决 allowCredentials=true 时不能使用 "*" 的问题)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:5173",
            "http://127.0.0.1:5173"
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        
        // 允许发送Cookie
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}