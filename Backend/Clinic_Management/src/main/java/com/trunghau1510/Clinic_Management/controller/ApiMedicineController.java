package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.service.MedicineService;
import com.trunghau1510.Clinic_Management.dto.response.MedicineResponse;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicines")
public class ApiMedicineController {
    private final MedicineService medicineService;

    public ApiMedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("")
    public ApiResponse<Page<MedicineResponse>> getAllMedicines(Pageable pageable) {
        Page<MedicineResponse> medicines = medicineService.getAllMedicines(pageable);
        return ApiResponse.<Page<MedicineResponse>>builder()
                .result(medicines)
                .message("Lấy danh sách thuốc thành công")
                .count(medicines.getTotalElements())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MedicineResponse> getMedicineById(@PathVariable String id) {
        MedicineResponse response = medicineService.getMedicineById(id);
        if (response == null) {
            return ApiResponse.<MedicineResponse>builder()
                    .result(null)
                    .message("Không tìm thấy thuốc với id: " + id)
                    .build();
        }
        return ApiResponse.<MedicineResponse>builder()
                .result(response)
                .message("Lấy thông tin thuốc thành công")
                .build();
    }
}
