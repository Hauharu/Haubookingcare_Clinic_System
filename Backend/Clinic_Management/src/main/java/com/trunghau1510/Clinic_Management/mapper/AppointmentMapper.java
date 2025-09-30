package com.trunghau1510.Clinic_Management.mapper;

import org.mapstruct.*;
import com.trunghau1510.Clinic_Management.entity.Doctor;
import com.trunghau1510.Clinic_Management.entity.Patient;
import com.trunghau1510.Clinic_Management.entity.Appointment;
import com.trunghau1510.Clinic_Management.dto.response.AppointmentResponse;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentCreationRequest;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(target = "patientName", expression = "java(getPatientName(appointment.getPatient()))")
    @Mapping(target = "doctorName", expression = "java(getDoctorName(appointment.getDoctor()))")
    AppointmentResponse toAppointmentResponse(Appointment appointment);

    Appointment toEntity(AppointmentCreationRequest request);

    void updateEntity(@MappingTarget Appointment appointment, AppointmentUpdateRequest request);

    default String getPatientName(Patient patient) {
        return patient != null && patient.getUser() != null
            ? patient.getUser().getFirstName() + " " + patient.getUser().getLastName()
            : null;
    }
    default String getDoctorName(Doctor doctor) {
        return doctor != null && doctor.getUser() != null
            ? doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName()
            : null;
    }
}
