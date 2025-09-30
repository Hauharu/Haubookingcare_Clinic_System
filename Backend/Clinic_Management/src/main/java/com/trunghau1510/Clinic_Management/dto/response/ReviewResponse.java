package com.trunghau1510.Clinic_Management.dto.response;

import lombok.Data;
import java.time.Instant;

@Data
public class ReviewResponse {

    private String id;
    private String doctorId;
    private String patientId;
    private String appointmentId;
    private Integer rating;
    private String comment;
    private Instant reviewDate;
    private String doctorResponse;
    private Instant responseDate;
    private Boolean isVisible;
    private DoctorResponse doctor;
    private UserResponse patient;
}
