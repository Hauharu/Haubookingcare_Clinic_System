package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.HealthRecordCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.HealthRecordUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.HealthRecordResponse;

import java.util.List;

public interface HealthRecordService {

    HealthRecordResponse addHealthRecord(HealthRecordCreationRequest request);
    HealthRecordResponse updateHealthRecord(String healthRecordId, HealthRecordUpdateRequest request);
    List<HealthRecordResponse> getHealthRecordsByUserId(String userId);
    HealthRecordResponse getHealthRecordByAppointmentId(String appointmentId);
}
