package com.trunghau1510.Clinic_Management.controller;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.repository.UserRepository;
import com.trunghau1510.Clinic_Management.service.AppointmentService;
import org.springframework.security.core.context.SecurityContextHolder;
import com.trunghau1510.Clinic_Management.dto.response.AppointmentResponse;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentStatusUpdateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiAppointmentController {

    AppointmentService appointmentService;
    PaginationConfig paginationConfig;
    UserRepository userRepository;

    @PostMapping
    public ApiResponse<AppointmentResponse> bookAppointment(@RequestBody AppointmentCreationRequest request) {
        AppointmentResponse response = appointmentService.registerAppointment(request);
        return ApiResponse.<AppointmentResponse>builder()
                .result(response)
                .message("Đặt lịch hẹn thành công")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AppointmentResponse> updateAppointment(@PathVariable String id, @RequestBody AppointmentUpdateRequest request) {
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        return ApiResponse.<AppointmentResponse>builder()
                .result(response)
                .message("Cập nhật lịch hẹn thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ApiResponse.<Void>builder()
                .message("Xóa lịch hẹn thành công")
                .build();
    }

    @GetMapping
    public ApiResponse<List<AppointmentResponse>> getAppointments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int pageNumber = (page != null) ? page : paginationConfig.getDefaultPage();
        int pageSize = (size != null) ? size : paginationConfig.getDefaultSize();
        // Tự động lấy id user hiện tại
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        String patientId = null;
        String doctorId = null;
        if (currentUser != null) {
            if (currentUser.getRole() == Role.PATIENT) {
                patientId = currentUser.getId();
            } else if (currentUser.getRole() == Role.DOCTOR) {
                doctorId = currentUser.getId();
            }
        }
        List<AppointmentResponse> appointments = appointmentService.getAppointments(patientId, doctorId, pageNumber, pageSize);
        return ApiResponse.<List<AppointmentResponse>>builder()
                .result(appointments)
                .message("Lấy danh sách lịch hẹn thành công")
                .count(appointments.size())
                .build();
    }

    // Dành cho bác sĩ để cập nhật trạng thái lịch hẹn
    @PatchMapping("/{id}/status")
    public ApiResponse<AppointmentResponse> updateStatus(@PathVariable String id, @RequestBody AppointmentStatusUpdateRequest request) {
        AppointmentResponse response = appointmentService.updateStatus(id, request);
        return ApiResponse.<AppointmentResponse>builder()
                .result(response)
                .message("Cập nhật trạng thái lịch hẹn thành công")
                .build();
    }
}
