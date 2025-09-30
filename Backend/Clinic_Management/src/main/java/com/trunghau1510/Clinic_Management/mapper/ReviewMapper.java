package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.response.ReviewResponse;
import com.trunghau1510.Clinic_Management.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {DoctorMapper.class, UserMapper.class})
public interface ReviewMapper {
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "doctor", target = "doctor")
    @Mapping(source = "patient.user", target = "patient")
    ReviewResponse toReviewResponse(Review review);
}