package com.trunghau1510.Clinic_Management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentCreationRequest {

    String patientId;
    String doctorId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime appointmentTime;
    Integer durationMinutes;
    String reason;
    String consultationType;
    String videoCallLink;
}
