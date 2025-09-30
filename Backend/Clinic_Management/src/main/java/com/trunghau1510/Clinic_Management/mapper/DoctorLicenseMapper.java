package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorLicenseResponse;
import com.trunghau1510.Clinic_Management.entity.DoctorLicense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {DoctorMapper.class, UserMapper.class})
public interface DoctorLicenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "verificationDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DoctorLicense toDoctorLicense(DoctorLicenseCreationRequest request);

    @Mapping(source = "entity.doctor", target = "doctor")
    DoctorLicenseResponse toDoctorLicenseResponse(DoctorLicense entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDoctorLicense(@MappingTarget DoctorLicense entity, DoctorLicenseUpdateRequest request);

    // Thêm phương thức chuyển đổi LocalDateTime -> Instant
    default Instant map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    // Thêm phương thức chuyển đổi Instant -> LocalDateTime
    default LocalDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}