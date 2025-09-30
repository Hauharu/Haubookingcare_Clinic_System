package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.entity.Notification;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.enums.NotificationType;
import com.trunghau1510.Clinic_Management.repository.NotificationRepository;
import com.trunghau1510.Clinic_Management.service.DeviceTokenRedisService;
import com.trunghau1510.Clinic_Management.service.FcmService;
import com.trunghau1510.Clinic_Management.service.NotificationService;
import com.trunghau1510.Clinic_Management.dto.response.NotificationResponse;
import com.trunghau1510.Clinic_Management.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplement implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final DeviceTokenRedisService deviceTokenRedisService;
    private final FcmService fcmService;

    // Tạo mới notification và gửi FCM
    @Override
    public void createNotification(User user, String title, String content) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRead(false);
        notification.setType(NotificationType.APPOINTMENT);
        notificationRepository.save(notification);

        // Lấy device token từ Redis và gửi FCM
        String deviceToken = deviceTokenRedisService.getDeviceToken(user.getId());
        if (deviceToken != null && !deviceToken.isEmpty()) {
            fcmService.sendNotification(deviceToken, title, content);
        }
    }

    @Override
    public List<NotificationResponse> getNotificationResponsesForUserId(String userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllNotificationsForUser(String userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        notificationRepository.deleteAll(notifications);
    }

    @Override
    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }

    @Override
    public void markOneAsRead(String userId, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null && notification.getUser().getId().equals(userId)) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }
}
