package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.response.ReviewResponse;
import com.trunghau1510.Clinic_Management.dto.request.ReviewCreationRequest;
import com.trunghau1510.Clinic_Management.entity.*;
import com.trunghau1510.Clinic_Management.mapper.ReviewMapper;
import com.trunghau1510.Clinic_Management.repository.*;
import com.trunghau1510.Clinic_Management.service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImplement implements ReviewService {
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    UserRepository userRepository;
    PatientRepository patientRepository;
    AppointmentRepository appointmentRepository;

    @Override
    public ReviewResponse addReview(ReviewCreationRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_EXISTS));
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_EXISTS));
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (reviewRepository.findByAppointment_Id(appointment.getId()) != null)
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);

        Review review = Review.builder()
                .appointment(appointment)
                .doctor(appointment.getDoctor())
                .patient(patient)
                .rating(request.getRating().intValue())
                .comment(request.getComment())
                .reviewDate(Instant.now())
                .build();

        review = reviewRepository.save(review);

        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public ReviewResponse updateDoctorResponse(String reviewId, String doctorResponse, Principal principal) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) throw new AppException(ErrorCode.REVIEW_NOT_FOUND);
        User user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        if (!review.getDoctor().getUser().getId().equals(user.getId()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        review.setDoctorResponse(doctorResponse);
        review.setResponseDate(Instant.now());
        review = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsOfDoctor(String doctorId, int page, int size) {
        return reviewRepository.findByDoctor_IdAndIsVisibleTrueOrderByReviewDateDesc(doctorId, PageRequest.of(page, size))
            .stream().map(reviewMapper::toReviewResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getAllReviews(int page, int size) {
        return reviewRepository.findByIsVisibleTrueOrderByRatingDesc(PageRequest.of(page, size))
                .getContent() // Lấy danh sách review thực tế
                .stream().map(reviewMapper::toReviewResponse).collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReviewByAppointmentId(String appointmentId) {
        Review review = reviewRepository.findByAppointment_Id(appointmentId);
        return reviewMapper.toReviewResponse(review);
    }
}
