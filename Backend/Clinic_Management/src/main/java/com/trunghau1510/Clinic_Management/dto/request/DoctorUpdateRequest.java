package com.trunghau1510.Clinic_Management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorUpdateRequest extends UserUpdateRequest {
    String specialtyId;

    @Min(value = 0, message = "Số năm kinh nghiệm không được âm")
    @Max(value = 50, message = "Số năm kinh nghiệm không được vượt quá 50")
    Integer yearsExperience;

    @Size(max = 1000, message = "Tiểu sử không được vượt quá 1000 ký tự")
    String biography;

    @DecimalMin(value = "0.0", inclusive = false, message = "Phí khám phải lớn hơn 0")
    BigDecimal consultationFee;

    // Thêm trường để hiển thị avatar hiện tại
    String avatarUrl;

    // Thêm trường ��ể upload avatar mới
    org.springframework.web.multipart.MultipartFile avatar;
}
