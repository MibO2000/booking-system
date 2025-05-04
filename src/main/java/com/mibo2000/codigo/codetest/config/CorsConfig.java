package com.mibo2000.codigo.codetest.config;

import com.mibo2000.codigo.codetest.exception.UnexpectedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CorsConfig {
    @Value("${booking.allowedUrls}")
    private String[] allowedUrls;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Allowed Urls : {}", Arrays.toString(allowedUrls));
        if (allowedUrls == null || allowedUrls.length == 0) {
            throw new UnexpectedException("There is no allowed frontend urls.");
        }

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedUrls));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
