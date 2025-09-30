package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StatsRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT COUNT(a.id), SUM(i.amount), YEAR(a.appointmentTime), QUARTER(a.appointmentTime), MONTH(a.appointmentTime) " +
           "FROM Appointment a JOIN a.invoice i " +
           "WHERE a.status = 'Completed' AND i.status = 'Paid' " +
           "AND (:year IS NULL OR YEAR(a.appointmentTime) = :year) " +
           "AND (:quarter IS NULL OR QUARTER(a.appointmentTime) = :quarter) " +
           "AND (:month IS NULL OR MONTH(a.appointmentTime) = :month) " +
           "GROUP BY YEAR(a.appointmentTime), QUARTER(a.appointmentTime), MONTH(a.appointmentTime) " +
           "ORDER BY YEAR(a.appointmentTime), MONTH(a.appointmentTime)")
    List<Object[]> statsCountExaminedTotalAmount(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("month") Integer month);

    @Query("SELECT COUNT(a.id), a.reason, YEAR(a.appointmentTime), QUARTER(a.appointmentTime), MONTH(a.appointmentTime) " +
           "FROM Appointment a " +
           "WHERE a.status = 'Completed' " +
           "AND (:year IS NULL OR YEAR(a.appointmentTime) = :year) " +
           "AND (:quarter IS NULL OR QUARTER(a.appointmentTime) = :quarter) " +
           "AND (:month IS NULL OR MONTH(a.appointmentTime) = :month) " +
           "GROUP BY YEAR(a.appointmentTime), QUARTER(a.appointmentTime), MONTH(a.appointmentTime), a.reason " +
           "ORDER BY YEAR(a.appointmentTime), MONTH(a.appointmentTime)")
    List<Object[]> statsDiagnosedCountExamined(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("month") Integer month);
}
