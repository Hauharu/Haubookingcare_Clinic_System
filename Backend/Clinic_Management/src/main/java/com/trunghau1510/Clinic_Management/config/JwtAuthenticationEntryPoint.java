package com.trunghau1510.Clinic_Management.config;

import java.io.IOException;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response , AuthenticationException authException) throws IOException  {
        ErrorCode errorCode = ErrorCode.NONE_YET_TOKEN;
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().code(errorCode.getCode()).message(errorCode.getMsg()).build();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}