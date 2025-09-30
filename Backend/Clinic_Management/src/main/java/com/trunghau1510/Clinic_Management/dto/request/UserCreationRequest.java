package com.trunghau1510.Clinic_Management.dto.request;

import com.trunghau1510.Clinic_Management.entity.enums.Gender;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import com.trunghau1510.Clinic_Management.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 5, message = "Tên đăng nhập phải có ít nhất 5 ký tự")
    String username;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    String password;

    String confirmPassword;

    @Size(min = 1, message = "Họ không được để trống")
    String firstName;

    @Size(min = 1, message = "Tên không được để trống")
    String lastName;

    @Size(min = 10, message = "Email phải có ít nhất 10 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email không đúng định dạng")
    String email;

    @Size(min = 10, message = "Số điện thoại phải có ít nhất 10 ký tự")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại không đúng định dạng")
    String phoneNumber;

    @Size(min = 10, message = "Địa chỉ phải có ít nhất 10 ký tự")
    String address;

    @DobConstraint(min = 18, message = "Ngày sinh không hợp lệ, bạn phải đủ 18 tuổi")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;

    Gender gender;

    Role role;

    MultipartFile avatar;

    @AssertTrue(message = "Mật khẩu xác nhận không khớp")
    private boolean isPasswordsMatch() {
        return password == null || password.equals(confirmPassword);
    }
}