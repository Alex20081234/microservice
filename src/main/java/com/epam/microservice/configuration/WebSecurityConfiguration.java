package com.epam.microservice.configuration;

import com.epam.microservice.security.AuthTokenFilter;
import com.epam.microservice.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class WebSecurityConfiguration {
    private final JwtService jwtService;

    @Bean
    public AuthTokenFilter filter() {
        return new AuthTokenFilter(jwtService);
    }

    @Bean
    public FilterRegistrationBean<AuthTokenFilter> loggingFilter() {
        FilterRegistrationBean<AuthTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
