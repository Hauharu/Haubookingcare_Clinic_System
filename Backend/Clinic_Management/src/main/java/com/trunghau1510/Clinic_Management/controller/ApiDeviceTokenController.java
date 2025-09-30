package com.trunghau1510.Clinic_Management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.service.DeviceTokenRedisService;

@RestController
@RequestMapping("/api/device-token")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiDeviceTokenController {

    DeviceTokenRedisService deviceTokenRedisService;

    @PostMapping
    public ApiResponse<Void> saveDeviceToken(@RequestParam String userId, @RequestParam String deviceToken) {
        deviceTokenRedisService.saveDeviceToken(userId, deviceToken);
        return ApiResponse.<Void>builder()
                .message("Lưu device token thành công")
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteDeviceToken(@RequestParam String userId) {
        deviceTokenRedisService.deleteDeviceToken(userId);
        return ApiResponse.<Void>builder()
                .message("Xóa device token thành công")
                .build();
    }
}
