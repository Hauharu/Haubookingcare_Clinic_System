package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.response.NotificationResponse;
import com.trunghau1510.Clinic_Management.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(target = "isRead", source = "read")
    @Mapping(target = "userId", source = "user.id")
    NotificationResponse toDTO(Notification notification);
}
