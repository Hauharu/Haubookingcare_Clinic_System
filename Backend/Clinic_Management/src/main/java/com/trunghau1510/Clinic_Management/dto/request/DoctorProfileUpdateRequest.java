package com.trunghau1510.Clinic_Management.dto.request;

import com.trunghau1510.Clinic_Management.entity.enums.Gender;
import com.trunghau1510.Clinic_Management.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorProfileUpdateRequest {
    String id;
    String firstName;
    String lastName;
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại không đúng định dạng")
    String phoneNumber;
    String address;
    @DobConstraint(min = 18, message = "Ngày sinh không hợp lệ, bạn phải đủ 18 tuổi")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;
    Gender gender;
    MultipartFile avatar;
    String specialtyId;
    @Min(value = 0, message = "Số năm kinh nghiệm không được âm")
    @Max(value = 50, message = "Số năm kinh nghiệm không được vượt quá 50")
    Integer yearsExperience;
    @Size(max = 1000, message = "Tiểu sử không được vượt quá 1000 ký tự")
    String biography;
    @DecimalMin(value = "0.0", inclusive = false, message = "Phí khám phải lớn hơn 0")
    BigDecimal consultationFee;
}

