package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, String> {
    boolean existsByName(String name);
}

