package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.UserCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.UserUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.UserResponse;
import com.trunghau1510.Clinic_Management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "avatar", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}