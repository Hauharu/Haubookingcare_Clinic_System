package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {
    Optional<TimeSlot> findByDoctor_IdAndSlotDateAndStartTimeAndIsBooked(String doctorId, LocalDate slotDate, LocalTime startTime, Boolean isBooked);
    void deleteByDoctor_IdAndSlotDateInAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(String doctorId, java.util.List<java.time.LocalDate> slotDates, java.time.LocalTime startTime, java.time.LocalTime endTime);

    Optional<TimeSlot> findByDoctor_IdAndSlotDateAndStartTime(String doctorId, LocalDate slotDate, LocalTime startTime);

    @Query("SELECT t FROM TimeSlot t " + "WHERE t.doctor.id = :doctorId " +
            "AND t.slotDate >= :today " +
            "AND (:slotDate IS NULL OR t.slotDate = :slotDate) " +
            "AND (:startTime IS NULL OR t.startTime = :startTime) " +
            "AND (t.isBooked = false OR t.isBooked IS NULL)")
    Page<TimeSlot> findFutureAvailableSlotsByDoctorAndFilter(
            @Param("doctorId") String doctorId,
            @Param("today") LocalDate today,
            @Param("slotDate") LocalDate slotDate,
            @Param("startTime") LocalTime startTime,
            Pageable pageable
    );

    @Query("SELECT t FROM TimeSlot t " +
            "WHERE t.slotDate >= :today " +
            "AND (:slotDate IS NULL OR t.slotDate = :slotDate) " +
            "AND (:startTime IS NULL OR t.startTime = :startTime) " +
            "AND (t.isBooked = false OR t.isBooked IS NULL)")
    Page<TimeSlot> findFutureAvailableSlotsAndFilter(
            @Param("today") LocalDate today,
            @Param("slotDate") LocalDate slotDate,
            @Param("startTime") LocalTime startTime,
            Pageable pageable
    );

}
