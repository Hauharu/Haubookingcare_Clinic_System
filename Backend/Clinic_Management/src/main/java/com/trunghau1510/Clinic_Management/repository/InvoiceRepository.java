package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    Optional<Invoice> findByAppointmentId(String appointmentId);

    @Query("SELECT i FROM Invoice i WHERE i.appointment.patient.id = :userId OR i.appointment.doctor.id = :userId")
    List<Invoice> findByAppointmentPatientIdOrAppointmentDoctorId(@Param("userId") String patientId, @Param("userId") String doctorId);
}
