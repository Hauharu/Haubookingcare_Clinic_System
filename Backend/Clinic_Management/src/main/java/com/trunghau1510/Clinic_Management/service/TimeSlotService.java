package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.response.TimeSlotResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface TimeSlotService {

    Page<TimeSlotResponse> findTimeSlot(Map<String, String> params);
}

