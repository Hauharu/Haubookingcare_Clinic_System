package com.trunghau1510.Clinic_Management.exception;

import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    // Xử lý tất cả exception không xác định
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Exception: ", ex);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    // Xử lý AppException do service/controller ném ra
    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMsg())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Xử lý khi user không có quyền truy cập
    @ExceptionHandler(AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        log.warn("Authorization denied: {}", ex.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMsg())
                        .build());
    }

    // Xử lý lỗi validation từ @Valid (@Size, @Pattern, custom constraint) cho REST API
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleRestApiValidation(MethodArgumentNotValidException ex) {
        // Lấy defaultMessage từ field error đầu tiên
        String enumKey = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "INVALID_INPUT";
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        Map<String, Object> attributes = null;

        try {
            // Chuyển defaultMessage thành ErrorCode enum
            errorCode = ErrorCode.valueOf(enumKey);
            var constraintViolation = ex.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info("Validation attributes: {}", attributes);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid ErrorCode: {}", enumKey, e);
        }

        // Tạo response, thay {min} bằng giá trị thực tế nếu có
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(Objects.nonNull(attributes) ? mapAttribute(errorCode.getMsg(), attributes) : errorCode.getMsg())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Thay {min} trong message bằng giá trị thực tế
    private String mapAttribute(String msg, Map<?, ?> attributes) {
        Object minValueObj = attributes.get(MIN_ATTRIBUTE);
        String minValue = minValueObj instanceof Number ? String.valueOf(minValueObj) : "";
        return msg.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
