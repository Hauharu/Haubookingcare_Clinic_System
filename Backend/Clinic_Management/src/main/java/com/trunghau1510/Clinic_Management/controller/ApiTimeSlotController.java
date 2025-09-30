package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.response.TimeSlotResponse;
import com.trunghau1510.Clinic_Management.service.TimeSlotService;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiTimeSlotController {

    TimeSlotService timeSlotService;

    @GetMapping("/find_slot")
    public ApiResponse<Page<TimeSlotResponse>> findTimeSlots(@RequestParam Map<String, String> params) {
        Page<TimeSlotResponse> timeSlots = timeSlotService.findTimeSlot(params);
        return ApiResponse.<Page<TimeSlotResponse>>builder()
            .result(timeSlots)
            .message("Lấy danh sách khung giờ thành công")
            .count(timeSlots.getTotalElements())
            .build();
    }
}
