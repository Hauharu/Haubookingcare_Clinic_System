package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.MedicinePrescription;
import com.trunghau1510.Clinic_Management.entity.MedicinePrescriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicinePrescriptionRepository extends JpaRepository<MedicinePrescription, MedicinePrescriptionId> {
}

