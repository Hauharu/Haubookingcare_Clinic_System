package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorScheduleResponse;

import java.util.List;

public interface DoctorScheduleService {

    DoctorScheduleResponse registerDoctorSchedule(DoctorScheduleCreationRequest request);
    DoctorScheduleResponse updateDoctorSchedule(String scheduleId, DoctorScheduleUpdateRequest request);
    List<DoctorScheduleResponse> getDoctorScheduleById(String doctorId);
    void deleteSchedule(String scheduleId);
}

