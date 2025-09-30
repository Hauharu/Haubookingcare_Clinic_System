package com.trunghau1510.Clinic_Management.dto.request;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Positive;
    import lombok.*;
    import lombok.experimental.FieldDefaults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class VNPayCreationRequest {

        String invoiceId;

        @NotNull(message = "Số tiền là bắt buộc")
        @Positive(message = "Số tiền phải lớn hơn 0")
        Long amount;

        @NotBlank(message = "Thông tin thanh toán là bắt buộc")
        String orderInfo;

        String returnUrl;
    }
