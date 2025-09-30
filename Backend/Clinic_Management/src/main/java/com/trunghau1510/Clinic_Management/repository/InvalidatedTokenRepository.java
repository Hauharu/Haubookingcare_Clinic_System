package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    int deleteInvalidatedTokenByExpiryTimeBefore(Instant expiryTimeBefore);
}
