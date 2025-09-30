package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.HealthRecordCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.HealthRecordUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.HealthRecordResponse;
import com.trunghau1510.Clinic_Management.entity.HealthRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {UserMapper.class})
public interface HealthRecordMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    HealthRecord toHealthRecord(HealthRecordCreationRequest request);

    @Mapping(source = "patient.user", target = "patientResponse")
    @Mapping(source = "user", target = "doctorResponse")
    @Mapping(target = "appointmentTime", expression = "java(mapAppointmentTime(healthRecord.getAppointment()))")
    HealthRecordResponse toHealthRecordResponse(HealthRecord healthRecord);

    default String mapAppointmentTime(com.trunghau1510.Clinic_Management.entity.Appointment appointment) {
        if (appointment == null || appointment.getAppointmentTime() == null) {
            return "Chưa cập nhật";
        }
        return appointment.getAppointmentTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    default java.time.Instant map(java.time.LocalDateTime value) {
        return value == null ? null : value.atZone(java.time.ZoneId.systemDefault()).toInstant();
    }

    void updateHealthRecord(@MappingTarget HealthRecord healthRecord, HealthRecordUpdateRequest request);
}