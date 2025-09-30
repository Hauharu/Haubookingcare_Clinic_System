package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorScheduleResponse;
import com.trunghau1510.Clinic_Management.entity.Doctor;
import com.trunghau1510.Clinic_Management.entity.DoctorSchedule;
import com.trunghau1510.Clinic_Management.entity.enums.DayOfWeek;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.DoctorScheduleMapper;
import com.trunghau1510.Clinic_Management.repository.DoctorRepository;
import com.trunghau1510.Clinic_Management.repository.DoctorScheduleRepository;
import com.trunghau1510.Clinic_Management.repository.TimeSlotRepository;
import com.trunghau1510.Clinic_Management.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImplement implements DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorScheduleMapper mapper;

    @Override
    @CacheEvict(value = "schedules", allEntries = true)
    public DoctorScheduleResponse registerDoctorSchedule(DoctorScheduleCreationRequest request) {
        Doctor doctor = doctorRepository.findByUserUsername(
            SecurityContextHolder.getContext().getAuthentication().getName())
            .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        DayOfWeek dayOfWeek = request.getDayOfWeek();
        if (dayOfWeek == null) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        // Kiểm tra lịch trùng
        List<DoctorSchedule> conflicts = scheduleRepository.findConflictingSchedules(
            doctor.getId(),
            dayOfWeek,
            request.getStartTime(),
            request.getEndTime()
        );
        if (!conflicts.isEmpty()) {
            throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
        }

        DoctorSchedule schedule = mapper.toDoctorSchedule(request);
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dayOfWeek);
        if (schedule.getIsAvailable() == null) {
            schedule.setIsAvailable(true);
        }
        return mapper.toDoctorScheduleResponse(scheduleRepository.save(schedule));
    }

    @Override
    @CacheEvict(value = "schedules", allEntries = true)
    public DoctorScheduleResponse updateDoctorSchedule(String scheduleId, DoctorScheduleUpdateRequest request) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        mapper.updateDoctorSchedule(schedule, request);

        if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        return mapper.toDoctorScheduleResponse(scheduleRepository.save(schedule));
    }

    @Override
    @Cacheable(value = "schedules", key = "#doctorId")
    public List<DoctorScheduleResponse> getDoctorScheduleById(String doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            return new ArrayList<>();
        }
        return scheduleRepository.findSchedulesByDoctorId(doctorId)
                .stream()
                .map(mapper::toDoctorScheduleResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "schedules", allEntries = true)
    @Transactional
    public void deleteSchedule(String scheduleId) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        // Xóa các slot liên quan
        String doctorId = schedule.getDoctor().getId();
        java.time.LocalTime startTime = schedule.getStartTime();
        java.time.LocalTime endTime = schedule.getEndTime();
        String dayOfWeek = schedule.getDayOfWeek().name();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<java.time.LocalDate> slotDates = new java.util.ArrayList<>();
        // Lấy 7 ngày tiếp theo có dayOfWeek trùng với lịch
        for (int i = 0; i < 7; i++) {
            java.time.LocalDate date = today.plusDays(i);
            if (date.getDayOfWeek().name().equals(dayOfWeek)) {
                slotDates.add(date);
            }
        }
        // Xóa slot
        if (!slotDates.isEmpty()) {
            scheduleRepository.flush();
            timeSlotRepository.deleteByDoctor_IdAndSlotDateInAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
                doctorId, slotDates, startTime, endTime);
        }
        scheduleRepository.delete(schedule);
    }
}
