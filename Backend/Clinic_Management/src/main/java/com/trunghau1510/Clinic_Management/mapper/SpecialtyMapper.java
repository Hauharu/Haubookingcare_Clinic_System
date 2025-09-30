package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.SpecialtyCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.SpecialtyUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.SpecialtyResponse;
import com.trunghau1510.Clinic_Management.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpecialtyMapper {

    SpecialtyResponse toSpecialtyResponse(Specialty specialty);

    @Mapping(target = "id", source = "id")
    Specialty toSpecialty(SpecialtyCreationRequest request);

    @Mapping(target = "id", source = "id")
    Specialty toSpecialty(SpecialtyUpdateRequest request);

}
