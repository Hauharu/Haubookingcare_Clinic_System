package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.DoctorCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorResponse;
import com.trunghau1510.Clinic_Management.entity.Doctor;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface DoctorMapper {
    Doctor toDoctor(DoctorCreationRequest request);
    DoctorResponse toDoctorResponse(Doctor doctor);
    default DoctorUpdateRequest toDoctorUpdateRequest(DoctorResponse response) {
        DoctorUpdateRequest dto = new DoctorUpdateRequest();
        if (response.getUser() != null) {
            dto.setId(response.getId());
            dto.setUsername(response.getUser().getUsername());
            dto.setFirstName(response.getUser().getFirstName());
            dto.setLastName(response.getUser().getLastName());
            dto.setEmail(response.getUser().getEmail());
            dto.setPhoneNumber(response.getUser().getPhoneNumber());
            dto.setDateOfBirth(response.getUser().getDateOfBirth());
            dto.setAddress(response.getUser().getAddress());
            dto.setGender(response.getUser().getGender());
            // Ánh xạ avatarUrl từ DoctorResponse sang DoctorUpdateRequest
            dto.setAvatarUrl(response.getAvatar());
        }
        if (response.getSpecialty() != null) {
            dto.setSpecialtyId(response.getSpecialty().getId());
        }
        dto.setYearsExperience(response.getYearsExperience());
        dto.setBiography(response.getBiography());
        dto.setConsultationFee(response.getConsultationFee());
        return dto;
    }
}
