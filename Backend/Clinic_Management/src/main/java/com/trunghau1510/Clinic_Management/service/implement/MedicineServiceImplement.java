package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.request.MedicineCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.MedicineUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.MedicineResponse;
import com.trunghau1510.Clinic_Management.entity.Medicine;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.MedicineMapper;
import com.trunghau1510.Clinic_Management.repository.MedicineRepository;
import com.trunghau1510.Clinic_Management.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicineServiceImplement implements MedicineService {
    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    @Override
    public void createMedicine(MedicineCreationRequest request) {
        if (medicineRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.MEDICINE_EXISTS);
        }
        Medicine medicine = medicineMapper.toMedicine(request);
        medicineRepository.save(medicine);
    }

    @Override
    public void updateMedicine(MedicineUpdateRequest request) {
        Medicine medicine = medicineRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));
        medicineMapper.updateMedicine(medicine, request);
        medicineRepository.save(medicine);
    }

    @Override
    public void deleteMedicine(String id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));
        medicineRepository.delete(medicine);
    }

    @Override
    public Page<MedicineResponse> getAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable).map(medicineMapper::toMedicineResponse);
    }

    @Override
    public MedicineResponse getMedicineById(String id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));
        return medicineMapper.toMedicineResponse(medicine);
    }
}
