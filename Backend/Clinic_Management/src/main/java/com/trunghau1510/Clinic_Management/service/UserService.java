package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.UserCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.UserUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.UserResponse;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    UserResponse createUser(UserCreationRequest request);
    UserResponse updateUser(String id, UserUpdateRequest request);
    UserResponse getUserById(String id);
    User getUserByUsername(String username) throws UsernameNotFoundException;
    Page<UserResponse> getUsersByRole(Role role, Pageable pageable);
    UserResponse getUserResponseByUsername(String username);
    void changePassword(String oldPassword, String newPassword);
}