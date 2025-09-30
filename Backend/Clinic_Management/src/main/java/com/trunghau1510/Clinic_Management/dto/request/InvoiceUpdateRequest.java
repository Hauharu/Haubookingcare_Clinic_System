package com.trunghau1510.Clinic_Management.dto.request;

import com.trunghau1510.Clinic_Management.entity.enums.InvoiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceUpdateRequest {

    @DecimalMin(value = "0.0", message = "Số tiền phải lớn hơn 0")
    BigDecimal amount;

    LocalDate dueDate;

    @NotNull(message = "Trạng thái là bắt buộc")
    InvoiceStatus status;
}
