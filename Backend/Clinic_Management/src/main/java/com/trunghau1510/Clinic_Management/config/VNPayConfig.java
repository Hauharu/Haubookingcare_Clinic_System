package com.trunghau1510.Clinic_Management.config;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayConfig {

    @Value("${vnpay.payment.url}")
    String vnpPayUrl;

    @Value("${vnpay.return.url}")
    String vnpReturnUrl;

    @Value("${vnpay.tmn.code}")
    String vnpTmnCode;

    @Value("${vnpay.hash.secret}")
    String secretKey;

    @Value("${vnpay.version}")
    String vnpVersion;

    @Value("${vnpay.command}")
    String vnpCommand;

    @Value("${vnpay.api.url}")
    String vnpApiUrl;

    public String getVnpOrderType() {
        return "other";
    }
}
