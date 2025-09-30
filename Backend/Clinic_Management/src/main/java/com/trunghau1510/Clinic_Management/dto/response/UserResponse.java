package com.trunghau1510.Clinic_Management.dto.response;

import com.trunghau1510.Clinic_Management.entity.enums.Gender;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UserResponse {

    String id;
    String username;
    // không trả về password
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String address;
    LocalDate dateOfBirth;
    Gender gender;
    Role role;
    String avatar;
    Boolean isActive;
    String doctorId;
}
