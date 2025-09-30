package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.SpecialtyResponse;
import com.trunghau1510.Clinic_Management.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class ApiSpecialtyController {
    private final SpecialtyService specialtyService;

    @GetMapping
    public ApiResponse<List<SpecialtyResponse>> getAllSpecialties() {
        List<SpecialtyResponse> responses = specialtyService.getAllSpecialtyResponses();
        return ApiResponse.<List<SpecialtyResponse>>builder()
                .result(responses)
                .message("Lấy danh sách chuyên khoa thành công")
                .count(responses.size())
                .build();
    }
}
