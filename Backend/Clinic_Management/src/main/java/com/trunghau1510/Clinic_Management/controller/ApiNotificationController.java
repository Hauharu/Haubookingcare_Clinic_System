package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.repository.UserRepository;
import com.trunghau1510.Clinic_Management.service.NotificationService;
import com.trunghau1510.Clinic_Management.dto.response.NotificationResponse;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiNotificationController {

    NotificationService notificationService;
    UserRepository userRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<NotificationResponse>> getNotifications(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("sub");
        if (username == null) {
            return ApiResponse.<List<NotificationResponse>>builder()
                .result(List.of())
                .message("Không xác thực được người dùng!")
                .count(0L)
                .build();
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ApiResponse.<List<NotificationResponse>>builder()
                .result(List.of())
                .message("Không tìm thấy người dùng!")
                .count(0L)
                .build();
        }
        List<NotificationResponse> notifications = notificationService.getNotificationResponsesForUserId(user.getId());
        return ApiResponse.<List<NotificationResponse>>builder()
            .result(notifications)
            .message("Lấy danh sách thông báo thành công!")
            .count(notifications.size())
            .build();
    }

    private User getUserFromJwt(Jwt jwt) {
        if (jwt == null) return null;
        String username = jwt.getClaimAsString("sub");
        if (username == null) return null;
        return userRepository.findByUsername(username).orElse(null);
    }

    @DeleteMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteAllNotifications(@AuthenticationPrincipal Jwt jwt) {
        User user = getUserFromJwt(jwt);
        notificationService.deleteAllNotificationsForUser(user.getId());
        return ApiResponse.<Void>builder()
            .message("Xóa tất cả thông báo thành công!")
            .build();
    }

    @PostMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        User user = getUserFromJwt(jwt);
        notificationService.markAllAsRead(user.getId());
        return ApiResponse.<Void>builder()
            .message("Đánh dấu tất cả thông báo đã đọc thành công!")
            .build();
    }

    @PostMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markOneAsRead(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        User user = getUserFromJwt(jwt);
        notificationService.markOneAsRead(user.getId(), id);
        return ApiResponse.<Void>builder()
                .message("Đánh dấu thông báo đã đọc thành công!")
                .build();
    }
}
