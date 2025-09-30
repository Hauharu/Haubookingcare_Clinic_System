package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.ReviewCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.ReviewResponse;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import com.trunghau1510.Clinic_Management.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/reviews")
public class ApiReviewController {

    ReviewService reviewService;
    PaginationConfig paginationConfig;

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("")
    public ApiResponse<ReviewResponse> addReview(@Valid @RequestBody ReviewCreationRequest request, Principal principal) {
        ReviewResponse response = reviewService.addReview(request, principal);
        return ApiResponse.<ReviewResponse>builder()
                .result(response)
                .message("Thêm đánh giá thành công")
                .build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PatchMapping("/{id}")
    public ApiResponse<ReviewResponse> updateDoctorResponse(@PathVariable String id, @RequestBody String doctorResponse, Principal principal) {
        ReviewResponse response = reviewService.updateDoctorResponse(id, doctorResponse, principal);
        return ApiResponse.<ReviewResponse>builder()
                .result(response)
                .message("Cập nhật phản hồi của bác sĩ thành công")
                .build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ApiResponse<List<ReviewResponse>> getReviewsOfDoctor(@PathVariable String doctorId,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size) {
        int pageNum = (page != null) ? page : paginationConfig.getDefaultPage();
        int pageSize = (size != null) ? size : paginationConfig.getDefaultSize();
        List<ReviewResponse> reviews = reviewService.getReviewsOfDoctor(doctorId, pageNum, pageSize);
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviews)
                .message("Lấy danh sách đánh giá của bác sĩ thành công")
                .count(reviews.size())
                .build();
    }

    @GetMapping("")
    public ApiResponse<List<ReviewResponse>> getAllReviews(@RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size) {
        int pageNum = (page != null) ? page : paginationConfig.getDefaultPage();
        int pageSize = (size != null) ? size : paginationConfig.getDefaultSize();
        List<ReviewResponse> reviews = reviewService.getAllReviews(pageNum, pageSize);
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviews)
                .message("Lấy danh sách đánh giá thành công")
                .count(reviews.size())
                .build();
    }

    @GetMapping("/appointment/{appointmentId}")
    public ApiResponse<ReviewResponse> getReviewByAppointmentId(@PathVariable String appointmentId) {
        ReviewResponse response = reviewService.getReviewByAppointmentId(appointmentId);
        return ApiResponse.<ReviewResponse>builder()
                .result(response)
                .message("Lấy đánh giá theo lịch hẹn thành công")
                .build();
    }
}
