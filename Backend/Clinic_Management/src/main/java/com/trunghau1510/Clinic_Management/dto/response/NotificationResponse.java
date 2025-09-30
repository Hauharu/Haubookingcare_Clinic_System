package com.trunghau1510.Clinic_Management.dto.response;

import java.time.Instant;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {

    String id;
    String title;
    String content;
    String type;
    boolean isRead;
    Instant createdAt;
    String userId;
}
