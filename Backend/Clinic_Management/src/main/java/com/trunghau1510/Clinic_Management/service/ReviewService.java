package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.response.ReviewResponse;
import com.trunghau1510.Clinic_Management.dto.request.ReviewCreationRequest;
import java.security.Principal;
import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(ReviewCreationRequest request, Principal principal);
    ReviewResponse updateDoctorResponse(String reviewId, String doctorResponse, Principal principal);
    List<ReviewResponse> getReviewsOfDoctor(String doctorId, int page, int size);
    List<ReviewResponse> getAllReviews(int page, int size);
    ReviewResponse getReviewByAppointmentId(String appointmentId);
}
