package com.trunghau1510.Clinic_Management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private int code;
    private String message;
    private long count;
    private T result;
}