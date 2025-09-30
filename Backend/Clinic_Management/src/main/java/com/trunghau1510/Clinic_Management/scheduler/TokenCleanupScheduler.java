package com.trunghau1510.Clinic_Management.scheduler;

import com.trunghau1510.Clinic_Management.service.InvalidatedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final InvalidatedTokenService invalidatedTokenService;

    // Chạy mỗi 1 tiếng (3600000 ms = 1 giờ)
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanUpTokens() {
        int deleted = invalidatedTokenService.cleanUpExpiredTokens();
        System.out.println("Đã xóa " + deleted + " Token đã hết hạn sử dụng.");
    }
}
