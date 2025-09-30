package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.MedicineCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.MedicineUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.MedicineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicineService {

    void createMedicine(MedicineCreationRequest request);
    void updateMedicine(MedicineUpdateRequest request);
    void deleteMedicine(String id);
    Page<MedicineResponse> getAllMedicines(Pageable pageable);
    MedicineResponse getMedicineById(String id);
}
