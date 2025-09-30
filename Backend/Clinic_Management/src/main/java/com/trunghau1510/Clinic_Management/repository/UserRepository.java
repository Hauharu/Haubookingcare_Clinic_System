package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
    Page<User> findByRole(Role role, Pageable pageable);
}