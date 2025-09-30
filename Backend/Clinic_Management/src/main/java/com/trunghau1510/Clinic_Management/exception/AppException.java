package com.trunghau1510.Clinic_Management.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class AppException extends RuntimeException {
    // Thay vì ném runtimeException thì ném AppException được globalExceptionHandler xử lý toàn cục
    ErrorCode errorCode;
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}