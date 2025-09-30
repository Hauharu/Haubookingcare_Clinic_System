package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.service.DeviceTokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenRedisServiceImplement implements DeviceTokenRedisService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveDeviceToken(String userId, String deviceToken) {
        String key = "user:deviceToken:" + userId;
        redisTemplate.opsForValue().set(key, deviceToken);
    }

    @Override
    public String getDeviceToken(String userId) {
        String key = "user:deviceToken:" + userId;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteDeviceToken(String userId) {
        String key = "user:deviceToken:" + userId;
        redisTemplate.delete(key);
    }
}

