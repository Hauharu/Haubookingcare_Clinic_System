package com.trunghau1510.Clinic_Management.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {

    INVALID_INPUT(1000, "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1001, "User không tồn tại", HttpStatus.NOT_FOUND),
    PASSWORD_FAIL(1002, "Sai mật khẩu", HttpStatus.UNAUTHORIZED),
    IS_ACTIVE_FALSE(1003, "Tài khoản chưa được kích hoạt", HttpStatus.UNAUTHORIZED),
    NONE_YET_TOKEN(1004, "Bạn chưa có token hoặc token đã bị thu hồi", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "Không có quyền thực hiện", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1006, "Không đăng nhập thành công", HttpStatus.UNAUTHORIZED),
    ENTITY_EXISTS(1007, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED(1024, "Không thể upload file", HttpStatus.INTERNAL_SERVER_ERROR),
    DOCTOR_NOT_FOUND(1027, "Bác sĩ không tồn tại", HttpStatus.NOT_FOUND),
    SPECIALTY_NOT_FOUND(1028, "Chuyên khoa không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_OLD_PASSWORD(1030, "Mật khẩu cũ không đúng", HttpStatus.BAD_REQUEST),
    HEALTH_RECORD_NOT_EXISTS(1035, "Hồ sơ sức khỏe không tồn tại", HttpStatus.NOT_FOUND),
    PATIENT_NOT_EXISTS(1040, "Bệnh nhân không tồn tại", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_FOUND(1052, "Lịch làm việc không tồn tại", HttpStatus.NOT_FOUND),
    SCHEDULE_CONFLICT(1053, "Lịch làm việc bị trùng thời gian", HttpStatus.BAD_REQUEST),
    INVALID_TIME_RANGE(1054, "Thời gian bắt đầu phải trước thời gian kết thúc", HttpStatus.BAD_REQUEST),
    DOCTOR_LICENSE_NOT_EXISTED(1058, "Chứng chỉ hành nghề không tồn tại", HttpStatus.NOT_FOUND),
    DOCTOR_LICENSE_EXISTED(1059, "Bác sĩ đã có chứng chỉ hành nghề", HttpStatus.BAD_REQUEST),
    LICENSE_NUMBER_EXISTED(1060, "Số chứng chỉ đã tồn tại", HttpStatus.BAD_REQUEST),
    DOCTOR_NOT_EXISTED(1066, "Bác sĩ không tồn tại", HttpStatus.NOT_FOUND),
    MEDICINE_EXISTS(1040, "Thuốc đã tồn tại", HttpStatus.BAD_REQUEST),
    MEDICINE_NOT_FOUND(1041, "Thuốc không tồn tại", HttpStatus.NOT_FOUND),
    TIMESLOT_NOT_AVAILABLE(1042, "Khung giờ đã được đặt", HttpStatus.BAD_REQUEST),
    INVOICE_NOT_EXISTS(1043, "Hóa đơn không tồn tại", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOT_EXISTS(1045, "Lịch hẹn không tồn tại", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(1067, "Đánh giá không tồn tại", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS(1068, "Bạn đã đánh giá rồi", HttpStatus.BAD_REQUEST),
    PAYMENT_CREATION_FAILED(1069, "Phương thức thanh toán lỗi", HttpStatus.NOT_FOUND),
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa được phân loại", HttpStatus.INTERNAL_SERVER_ERROR);

    ErrorCode(int code, String msg, HttpStatus statusCode) {
        this.code = code;
        this.msg = msg;
        this.statusCode = statusCode;
    }

    final int code;
    final String msg;
    final HttpStatus statusCode;
}
