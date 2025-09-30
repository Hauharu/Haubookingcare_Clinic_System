package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.*;
import com.trunghau1510.Clinic_Management.dto.response.*;

import com.nimbusds.jose.JOSEException;
import java.text.ParseException;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException;
    void logout(LogoutRequest request) throws JOSEException, ParseException;
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
}