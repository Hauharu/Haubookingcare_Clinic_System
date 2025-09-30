    package com.trunghau1510.Clinic_Management.dto.response;

    import lombok.Builder;
    import lombok.Data;

    @Data
    @Builder
    public class PaypalResponse {

        private String approvalUrl;
        private String status;
        private String message;
    }

