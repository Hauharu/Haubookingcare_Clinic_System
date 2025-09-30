package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import com.trunghau1510.Clinic_Management.dto.response.TimeSlotResponse;
import com.trunghau1510.Clinic_Management.mapper.TimeSlotMapper;
import com.trunghau1510.Clinic_Management.repository.TimeSlotRepository;
import com.trunghau1510.Clinic_Management.service.TimeSlotService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeSlotServiceImplement implements TimeSlotService {

    TimeSlotRepository timeSlotRepository;
    TimeSlotMapper timeSlotMapper;
    PaginationConfig paginationConfig;

    @Override
    @Transactional(readOnly = true)
    public Page<TimeSlotResponse> findTimeSlot(Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", String.valueOf(paginationConfig.getDefaultPage())));
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(paginationConfig.getDefaultSize())));
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("slotDate").ascending().and(Sort.by("startTime").ascending()));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        // Lấy filter ngày và giờ nếu có
        LocalDate slotDate = null;
        LocalTime startTime = null;
        if (params.containsKey("slotDate") && params.get("slotDate") != null && !params.get("slotDate").isEmpty()) {
            slotDate = LocalDate.parse(params.get("slotDate"));
        }
        if (params.containsKey("startTime") && params.get("startTime") != null && !params.get("startTime").isEmpty()) {
            startTime = LocalTime.parse(params.get("startTime"));
        }

        if (params.containsKey("doctorId") && params.get("doctorId") != null && !params.get("doctorId").isEmpty()) {
            String doctorId = params.get("doctorId");
            return timeSlotRepository.findFutureAvailableSlotsByDoctorAndFilter(doctorId, today, slotDate, startTime, pageable)
                    .map(timeSlotMapper::mapToResponse);
        } else {
            return timeSlotRepository.findFutureAvailableSlotsAndFilter(today, slotDate, startTime, pageable)
                    .map(timeSlotMapper::mapToResponse);
        }
    }
}
