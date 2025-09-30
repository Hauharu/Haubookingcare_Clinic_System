package com.trunghau1510.Clinic_Management.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecialtyUpdateRequest {

    String id;

    @Size(max = 100, message = "Tên chuyên khoa không được vượt quá 100 ký tự")
    String name;

    @Size(max = 1000, message = "Mô tả chuyên khoa không được vượt quá 1000 ký tự")
    String description;
}
