package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findByDoctor_IdAndIsVisibleTrueOrderByReviewDateDesc(String doctorId, Pageable pageable);
    Page<Review> findByIsVisibleTrueOrderByRatingDesc(Pageable pageable);
    Review findByAppointment_Id(String appointmentId);
}
