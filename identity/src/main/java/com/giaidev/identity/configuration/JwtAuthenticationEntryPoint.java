package com.giaidev.identity.configuration;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giaidev.core.dto.ApiErrorResponse;
import com.giaidev.core.exception.CommonErrorCode;


import com.giaidev.core.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    private final BearerTokenAuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {

        CommonErrorCode errorCode = CommonErrorCode.UNAUTHENTICATED;

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        objectMapper.writeValue(
                response.getOutputStream(),
               ApiResponse.error(
                       errorCode.getCode(),
                       errorCode.getMessage()
               )
        );
    }
}