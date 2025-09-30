package com.trunghau1510.Clinic_Management.service;

public interface FcmService {

    void sendNotification(String deviceToken, String title, String body);
}

