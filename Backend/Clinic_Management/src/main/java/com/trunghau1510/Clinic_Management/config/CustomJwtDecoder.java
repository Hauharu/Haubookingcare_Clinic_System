package com.trunghau1510.Clinic_Management.config;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.experimental.NonFinal;
import lombok.RequiredArgsConstructor;
import javax.crypto.spec.SecretKeySpec;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.service.AuthenticationService;
import com.trunghau1510.Clinic_Management.dto.request.IntrospectRequest;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/*
 * Class CustomJwtDecoder thực hiện kiểm tra 2 bước:
 * 1. Kiểm tra tính hợp lệ token còn trong thời gian sử dụng nhưng đã logout gọi AuthenticationService.introspect
 * 2. Giải mã và xác minh chữ ký của token bằng NimbusJwtDecoder.
 */
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    @NonFinal
    String signerKey;

    AuthenticationService authenticationService;

    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(IntrospectRequest.builder().token(token).build());
            if (!response.isValid())
                throw new AppException(ErrorCode.NONE_YET_TOKEN);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS256");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS256).build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
