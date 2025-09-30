package com.trunghau1510.Clinic_Management.service;

public interface DeviceTokenRedisService {

    void saveDeviceToken(String userId, String deviceToken);
    String getDeviceToken(String userId);
    void deleteDeviceToken(String userId);
}
