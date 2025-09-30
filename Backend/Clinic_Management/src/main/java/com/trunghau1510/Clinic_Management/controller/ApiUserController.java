package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.ChangePasswordRequest;
import com.trunghau1510.Clinic_Management.dto.request.UserCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.UserUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.UserResponse;
import com.trunghau1510.Clinic_Management.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiUserController {

    UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@ModelAttribute @Valid UserCreationRequest request) {
        UserResponse response = userService.createUser(request);
        return ApiResponse.<UserResponse>builder()
                .result(response)
                .message("Tạo tài khoản người dùng thành công")
                .build();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("@userServiceImplement.getUserById(#userId).username == authentication.name")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable String userId,
            @ModelAttribute @Valid UserUpdateRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ApiResponse.<UserResponse>builder()
                .result(response)
                .message("Cập nhật thông tin người dùng thành công")
                .build();
    }

    @GetMapping("/{userId}")
    @PostAuthorize("returnObject.result.username == authentication.name")
    public ApiResponse<UserResponse> getUserById(@PathVariable String userId) {
        UserResponse response = userService.getUserById(userId);
        return ApiResponse.<UserResponse>builder()
                .result(response)
                .message("Lấy thông tin người dùng thành công")
                .build();
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserResponse response = userService.getUserResponseByUsername(auth.getName());
        return ApiResponse.<UserResponse>builder()
                .result(response)
                .message("Lấy thông tin nguười dùng hiện tại thành công")
                .build();
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getOldPassword(), request.getNewPassword());
        return ApiResponse.<Void>builder()
                .message("Đổi mật khẩu thành công")
                .build();
    }
}