package com.trunghau1510.Clinic_Management.dto.request;

import com.trunghau1510.Clinic_Management.entity.enums.DayOfWeek;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorScheduleUpdateRequest {

    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    Boolean isAvailable;
}
