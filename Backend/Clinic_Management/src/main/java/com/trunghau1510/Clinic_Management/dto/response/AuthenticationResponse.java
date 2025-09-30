package com.trunghau1510.Clinic_Management.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse {

    private String token;
    private boolean authenticated;
}