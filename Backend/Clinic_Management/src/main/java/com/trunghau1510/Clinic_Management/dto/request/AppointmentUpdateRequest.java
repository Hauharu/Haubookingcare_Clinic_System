package com.trunghau1510.Clinic_Management.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentUpdateRequest {

    LocalDateTime appointmentTime;
    Integer durationMinutes;
    String reason;
    String consultationType;
    String videoCallLink;
    String cancellationReason;
}
