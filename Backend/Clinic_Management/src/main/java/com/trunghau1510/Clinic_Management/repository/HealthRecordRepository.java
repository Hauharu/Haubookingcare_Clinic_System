package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.HealthRecord;
import com.trunghau1510.Clinic_Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, String> {

    List<HealthRecord> findByUser(User user);

    Optional<HealthRecord> findByAppointmentId(String appointmentId);

    @Query("SELECT hr FROM HealthRecord hr LEFT JOIN FETCH hr.appointment WHERE hr.patient.id = :patientId")
    List<HealthRecord> findByPatientIdWithAppointment(@Param("patientId") String patientId);
}
