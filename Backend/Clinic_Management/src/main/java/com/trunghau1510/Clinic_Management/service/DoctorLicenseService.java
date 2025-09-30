package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorLicenseResponse;

import java.util.List;

public interface DoctorLicenseService {

    DoctorLicenseResponse createLicense(DoctorLicenseCreationRequest request);
    DoctorLicenseResponse updateLicense(String id, DoctorLicenseUpdateRequest request);
    void deleteLicense(String id);
    DoctorLicenseResponse getLicenseByDoctorId(String doctorId);
    List<DoctorLicenseResponse> getAllUnverifiedLicenses();
    List<DoctorLicenseResponse> getAllVerifiedLicenses();
    void approveLicense(String id, boolean isApproved);
    List<DoctorLicenseResponse> getAllLicenses();
}
