package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorScheduleResponse;
import com.trunghau1510.Clinic_Management.entity.DoctorSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DoctorScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    DoctorSchedule toDoctorSchedule(DoctorScheduleCreationRequest request);

    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(target = "doctorName", expression = "java(schedule.getDoctor() != null && schedule.getDoctor().getUser() != null ? schedule.getDoctor().getUser().getFirstName() + \" \" + schedule.getDoctor().getUser().getLastName() : \"\")")
    @Mapping(source = "doctor.specialty.name", target = "specialtyName")
    DoctorScheduleResponse toDoctorScheduleResponse(DoctorSchedule schedule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    void updateDoctorSchedule(@MappingTarget DoctorSchedule schedule, DoctorScheduleUpdateRequest request);
}
