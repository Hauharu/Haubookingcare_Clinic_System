package com.trunghau1510.Clinic_Management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trunghau1510.Clinic_Management.entity.enums.DayOfWeek;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorScheduleResponse implements java.io.Serializable {

    String id;
    String doctorId;
    String doctorName;
    String specialtyName;
    DayOfWeek dayOfWeek;
    Boolean isAvailable;

    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime endTime;


}
