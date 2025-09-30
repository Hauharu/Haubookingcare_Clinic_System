package com.trunghau1510.Clinic_Management.dto.request;

import com.trunghau1510.Clinic_Management.entity.enums.Gender;
import com.trunghau1510.Clinic_Management.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    String id;

    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    String password;

    String confirmPassword;

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

    @Email(message = "Email không hợp lệ")
    @Size(min = 10, message = "Email phải có ít nhất 10 ký tự")
    String email;

    @Size(min = 5, message = "Tên đăng nhập phải có ít nhất 5 ký tự")
    String username;

    @AssertTrue(message = "CONFIRM_PASSWORD_NOT_MATCH")
    private boolean isPasswordsMatch() {
        return password == null || password.equals(confirmPassword);
    }
}