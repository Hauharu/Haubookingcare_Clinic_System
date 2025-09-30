package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Appointment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    List<Appointment> findByPatient_Id(String patientId, Pageable pageable);
    List<Appointment> findByDoctor_Id(String doctorId, Pageable pageable);
    List<Appointment> findByPatient_IdAndDoctor_Id(String patientId, String doctorId, Pageable pageable);
}
