package com.trunghau1510.Clinic_Management.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCreationRequest {

    String doctorId;
    String appointmentId;
    @DecimalMin(value = "0.0", message = "Đánh giá phải từ 0 đến 5")
    @DecimalMax(value = "5.0", message = "Đánh giá phải từ 0 đến 5")
    BigDecimal rating;

    @Size(max = 1000, message = "Nội dung nhận xét không được vượt quá 1000 ký tự")
    String comment;
}
