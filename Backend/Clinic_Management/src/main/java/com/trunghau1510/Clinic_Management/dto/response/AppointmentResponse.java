package com.trunghau1510.Clinic_Management.dto.response;

import com.trunghau1510.Clinic_Management.entity.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentResponse {

    String id;
    String patientId;
    String patientName;
    String doctorId;
    String doctorName;
    LocalDateTime appointmentTime;
    Integer durationMinutes;
    String reason;
    Status status;
    String consultationType;
    String videoCallLink;
    String cancellationReason;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
