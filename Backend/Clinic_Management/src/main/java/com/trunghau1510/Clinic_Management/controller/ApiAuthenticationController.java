package com.trunghau1510.Clinic_Management.controller;

import lombok.AccessLevel;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import com.nimbusds.jose.JOSEException;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import com.trunghau1510.Clinic_Management.dto.request.*;
import com.trunghau1510.Clinic_Management.dto.response.*;
import com.trunghau1510.Clinic_Management.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiAuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        var res = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(res)
                .message("Đăng nhập thành công")
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException , JOSEException {
        var res = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(res)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException , JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshTokenRequest request) throws ParseException , JOSEException {
        var res = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }
}