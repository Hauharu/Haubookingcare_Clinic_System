package com.trunghau1510.Clinic_Management.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeSlotResponse {

    String id;
    String doctorId;
    String doctorName;
    LocalDate slotDate;
    LocalTime startTime;
    LocalTime endTime;
    Boolean isBooked;
    String avatar;
}
