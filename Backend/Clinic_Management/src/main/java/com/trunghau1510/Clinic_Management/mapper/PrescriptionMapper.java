package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.PrescriptionCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.PrescriptionResponse;
import com.trunghau1510.Clinic_Management.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrescriptionMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true),
        @Mapping(target = "doctor", ignore = true),
        @Mapping(target = "patient", ignore = true),
        @Mapping(target = "healthRecord", ignore = true)
    })
    Prescription toPrescription(PrescriptionCreationRequest request);

    PrescriptionResponse toPrescriptionResponse(Prescription prescription);
}
