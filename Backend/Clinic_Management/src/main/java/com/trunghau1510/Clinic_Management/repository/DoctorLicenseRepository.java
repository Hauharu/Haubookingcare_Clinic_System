package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.DoctorLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface DoctorLicenseRepository extends JpaRepository<DoctorLicense, String> {

    @NonNull
    Optional<DoctorLicense> findByDoctorId(@NonNull String doctorId);
    void deleteByDoctorId(@NonNull String doctorId);
    @NonNull
    Optional<DoctorLicense> findByLicenseNumber(@NonNull String licenseNumber);
    @Query("SELECT COUNT(dl) > 0 FROM DoctorLicense dl WHERE dl.licenseNumber = :licenseNumber AND dl.id != :excludeId")
    boolean existsByLicenseNumberAndIdNot(@NonNull @Param("licenseNumber") String licenseNumber, @NonNull @Param("excludeId") String excludeId);

    @NonNull
    List<DoctorLicense> findAll();
}