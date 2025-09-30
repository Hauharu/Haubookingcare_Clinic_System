package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.response.TimeSlotResponse;
import com.trunghau1510.Clinic_Management.entity.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TimeSlotMapper {

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", expression = "java(mapDoctorName(timeSlot))")
    @Mapping(target = "avatar", expression = "java(mapDoctorAvatar(timeSlot))")
    TimeSlotResponse mapToResponse(TimeSlot timeSlot);

    default String mapDoctorName(TimeSlot timeSlot) {
        if (timeSlot.getDoctor() != null && timeSlot.getDoctor().getUser() != null) {
            return timeSlot.getDoctor().getUser().getFirstName() + " " + timeSlot.getDoctor().getUser().getLastName();
        }
        return null;
    }

    default String mapDoctorAvatar(TimeSlot timeSlot) {
        if (timeSlot.getDoctor() != null && timeSlot.getDoctor().getUser() != null) {
            return timeSlot.getDoctor().getUser().getAvatar();
        }
        return null;
    }
}
