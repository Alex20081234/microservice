package com.epam.microservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @InjectMocks
    private AuthTokenFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternalShouldPassWhenValidToken() throws ServletException, IOException {
        when(jwtService.parseJwt(any())).thenReturn("Valid token");
        when(jwtService.validateJwtToken(anyString())).thenReturn(true);
        filter.doFilterInternal(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldSendForbiddenWhenInvalidToken() throws ServletException, IOException {
        when(jwtService.parseJwt(any())).thenReturn(null);
        filter.doFilterInternal(request, response, chain);
        verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
        when(jwtService.parseJwt(any())).thenReturn("Invalid token");
        when(jwtService.validateJwtToken(anyString())).thenReturn(false);
        filter.doFilterInternal(request, response, chain);
        verify(response, times(2)).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
    }
}
