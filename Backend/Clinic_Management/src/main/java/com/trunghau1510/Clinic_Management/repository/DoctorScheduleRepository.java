package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.DoctorSchedule;
import com.trunghau1510.Clinic_Management.entity.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, String> {

    List<DoctorSchedule> findSchedulesByDoctorId(String doctorId);

    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor.id = :doctorId " +
           "AND ds.dayOfWeek = :dayOfWeek " +
           "AND ((ds.startTime <= :startTime AND ds.endTime > :startTime) " +
           "OR (ds.startTime < :endTime AND ds.endTime >= :endTime) " +
           "OR (ds.startTime >= :startTime AND ds.endTime <= :endTime))")
    List<DoctorSchedule> findConflictingSchedules(@Param("doctorId") String doctorId,
                                                  @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                  @Param("startTime") LocalTime startTime,
                                                  @Param("endTime") LocalTime endTime);
}
