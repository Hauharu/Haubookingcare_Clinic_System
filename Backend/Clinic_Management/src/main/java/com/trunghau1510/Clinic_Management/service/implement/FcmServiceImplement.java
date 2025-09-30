package com.trunghau1510.Clinic_Management.service.implement;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.trunghau1510.Clinic_Management.service.FcmService;
import org.springframework.stereotype.Service;

@Service
public class FcmServiceImplement implements FcmService {

    @Override
    public void sendNotification(String deviceToken, String title, String body) {
        Message message = Message.builder()
            .setToken(deviceToken)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            // TODO: Replace printStackTrace with robust logging or error handling
        }
    }
}
