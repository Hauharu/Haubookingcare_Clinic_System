package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.DoctorSearchRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {

    DoctorResponse updateDoctor(String id, DoctorUpdateRequest request);
    void deleteDoctorById(String id);
    DoctorResponse getDoctorById(String id);
    Page<DoctorResponse> getDoctors(Pageable pageable);
    Page<DoctorResponse> searchDoctorsByKeyword(DoctorSearchRequest request, Pageable pageable);
}