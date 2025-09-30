package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.MedicineCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.MedicineUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.MedicineResponse;
import com.trunghau1510.Clinic_Management.entity.Medicine;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MedicineMapper {
    Medicine toMedicine(MedicineCreationRequest request);
    MedicineResponse toMedicineResponse(Medicine medicine);
    void updateMedicine(@MappingTarget Medicine medicine, MedicineUpdateRequest request);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "pricePerUnit", source = "pricePerUnit")
    MedicineUpdateRequest toMedicineUpdateRequest(MedicineResponse response);
}
