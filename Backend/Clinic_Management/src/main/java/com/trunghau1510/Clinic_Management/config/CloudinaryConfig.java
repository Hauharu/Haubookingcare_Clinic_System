package com.trunghau1510.Clinic_Management.config;

import lombok.AccessLevel;
import com.cloudinary.Cloudinary;
import lombok.experimental.NonFinal;
import com.cloudinary.utils.ObjectUtils;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name}")
    @NonFinal // bỏ qua final để Spring có thể inject giá trị sau khi khởi tạo
    String cloudName;

    @Value("${cloudinary.api_key}")
    @NonFinal
    String apiKey;

    @Value("${cloudinary.api_secret}")
    @NonFinal
    String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }
}