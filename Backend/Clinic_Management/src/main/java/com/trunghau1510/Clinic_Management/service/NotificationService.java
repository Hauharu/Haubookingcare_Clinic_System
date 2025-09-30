package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.entity.User;
import java.util.List;
import com.trunghau1510.Clinic_Management.dto.response.NotificationResponse;

public interface NotificationService {

   List<NotificationResponse> getNotificationResponsesForUserId(String userId);
    void createNotification(User user, String title, String content);
    void deleteAllNotificationsForUser(String userId);
    void markAllAsRead(String userId);
    void markOneAsRead(String userId, String notificationId);
}
