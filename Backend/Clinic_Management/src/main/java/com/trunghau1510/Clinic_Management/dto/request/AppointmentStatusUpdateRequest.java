    package com.trunghau1510.Clinic_Management.dto.request;

    import com.trunghau1510.Clinic_Management.entity.enums.Status;
    import lombok.*;
    import lombok.experimental.FieldDefaults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class AppointmentStatusUpdateRequest {

        Status status;
    }

