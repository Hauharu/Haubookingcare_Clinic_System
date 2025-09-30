package com.trunghau1510.Clinic_Management.config;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "pagination")
public class PaginationConfig {

    int defaultPage;
    int defaultSize;
}