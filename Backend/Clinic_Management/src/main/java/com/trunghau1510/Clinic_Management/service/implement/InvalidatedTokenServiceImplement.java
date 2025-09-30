package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.repository.InvalidatedTokenRepository;
import com.trunghau1510.Clinic_Management.service.InvalidatedTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidatedTokenServiceImplement implements InvalidatedTokenService {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    @Transactional
    public int cleanUpExpiredTokens() {
        return invalidatedTokenRepository.deleteInvalidatedTokenByExpiryTimeBefore(Instant.now());
    }
}
