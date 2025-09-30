package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    Optional<Doctor> findByUserUsername(String username);
    Optional<Doctor> findByUserId(String userId);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Doctor d JOIN d.specialty s WHERE " +
            "LOWER(d.user.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.user.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "s.description LIKE CONCAT('%', :keyword, '%')")
    Page<Doctor> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
}