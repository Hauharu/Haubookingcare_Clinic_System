-- Xóa cơ sở dữ liệu trước đó (nếu tồn tại để update phiên bản mới nhất)
DROP DATABASE IF EXISTS railway;

-- Tạo cơ sở dữ liệu mới
CREATE DATABASE railway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Sử dụng cơ sở dữ liệu vừa tạo
USE railway;

-- Kiểm tra FK off
SET foreign_key_checks = 0;

-- Bảng user: Lưu thông tin chung cho tất cả người dùng (Patient, Doctor, Admin)
CREATE TABLE user (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()), 
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    phone_number VARCHAR(20) NOT NULL UNIQUE CHECK (phone_number REGEXP '^0[0-9]{9,10}$'),
    address TEXT NOT NULL,
    date_of_birth DATE NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NULL,
    role ENUM('PATIENT', 'DOCTOR', 'ADMIN') NOT NULL,
    avatar VARCHAR(255) NULL DEFAULT 'https://res.cloudinary.com/dwwfgtxv4/image/upload/v1754280542/anh-dai-dien_kz9gpm.png',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='Lưu trữ thông tin chung người dùng';

-- Bảng patient: Lưu thông tin bổ sung cho bệnh nhân
CREATE TABLE patient (
    id CHAR(36) PRIMARY KEY,
    medical_history TEXT,
    FOREIGN KEY (id) REFERENCES user(id) ON DELETE CASCADE
) COMMENT='Lưu trữ thông tin mở rộng về bệnh nhân';

-- Bảng doctor: Lưu thông tin bổ sung cho bác sĩ
CREATE TABLE doctor (
    id CHAR(36) PRIMARY KEY,
    specialty_id CHAR(36) NOT NULL,
    years_experience INT DEFAULT 0,
    biography TEXT,
    consultation_fee DECIMAL(10, 2) DEFAULT 0.00,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    FOREIGN KEY (id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (specialty_id) REFERENCES specialty(id) ON DELETE RESTRICT
) COMMENT='Lưu trữ thông tin mở rộng về bác sĩ';

-- Bảng specialty: Lưu trữ các chuyên khoa
CREATE TABLE specialty (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
) COMMENT='Lưu trữ các chuyên khoa';

-- Bảng doctor_license: Lưu trữ chứng chỉ hành nghề
CREATE TABLE doctor_license (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    doctor_id CHAR(36) NOT NULL,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    issuing_authority VARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NULL,
    scope_description TEXT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verification_date DATE NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
) COMMENT='Lưu trữ thông tin chứng chỉ hành nghề';

-- Bảng appointment: Lưu trữ lịch hẹn
CREATE TABLE appointment (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    patient_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,
    appointment_time DATETIME NOT NULL,
    duration_minutes INT DEFAULT 30,
    reason TEXT,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED_BY_PATIENT') NOT NULL DEFAULT 'SCHEDULED',
    consultation_type ENUM('OFFLINE', 'ONLINE') DEFAULT 'OFFLINE',
    video_call_link VARCHAR(255) NULL,
    cancellation_reason TEXT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE,
    INDEX idx_appointment_time (appointment_time),
    INDEX idx_doctor_time (doctor_id, appointment_time),
    INDEX idx_patient_time (patient_id, appointment_time)
) COMMENT='Lưu trữ lịch hẹn';

-- Bảng health_record: Lưu trữ hồ sơ sức khỏe
CREATE TABLE health_record (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    patient_id CHAR(36) NOT NULL,
    appointment_id CHAR(36) NULL,
    user_id CHAR(36) NULL,
    record_date DATE NOT NULL,
    symptoms TEXT,
    diagnosis TEXT,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (appointment_id) REFERENCES appointment(id) ON DELETE SET NULL
) COMMENT='Lưu trữ hồ sơ sức khỏe';

-- Bảng prescription: Lưu trữ đơn thuốc
CREATE TABLE prescription (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    health_record_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,
    patient_id CHAR(36) NOT NULL,
    issue_date DATE NOT NULL,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (health_record_id) REFERENCES health_record(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE
) COMMENT='Lưu trữ đơn thuốc liên quan đến hồ sơ sức khỏe';

-- Bảng medicine: Lưu trữ thông tin thuốc
CREATE TABLE medicine (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    unit VARCHAR(50) NOT NULL,
    price_per_unit DECIMAL(10, 2) DEFAULT 0.00
) COMMENT='Lưu trữ danh mục thuốc';

-- Bảng medicine_prescription
CREATE TABLE medicine_prescription (
    prescription_id CHAR(36) NOT NULL,
    medicine_id CHAR(36) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    dosage_instruction TEXT NOT NULL,
    PRIMARY KEY (prescription_id, medicine_id),
    FOREIGN KEY (prescription_id) REFERENCES prescription(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicine(id) ON DELETE RESTRICT
) COMMENT='Chi tiết các thuốc trong đơn thuốc';

-- Bảng time_slot: Lưu trữ slot trống của bác sĩ
CREATE TABLE time_slot (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    doctor_id CHAR(36) NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_slot (doctor_id, slot_date, start_time, end_time),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
) COMMENT='Lưu trữ slot trống của bác sĩ';

-- Bảng doctor_schedule: Lưu trữ giờ làm việc của bác sĩ
CREATE TABLE doctor_schedule (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    doctor_id CHAR(36) NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_doctor_day_time (doctor_id, day_of_week, start_time, end_time),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
) COMMENT='Lưu trữ giờ làm việc của bác sĩ';

-- Bảng invoice: Lưu trữ hóa đơn
CREATE TABLE invoice (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    appointment_id CHAR(36) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    status ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointment(id) ON DELETE CASCADE
) COMMENT='Lưu trữ thông tin hóa đơn';

-- Bảng payment: Lưu trữ thanh toán
CREATE TABLE payment (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    invoice_id CHAR(36) NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('VNPAY', 'PAYPAL', 'OTHER') NOT NULL,
    transaction_id VARCHAR(255) NULL UNIQUE,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE RESTRICT
) COMMENT='Lưu trữ thông tin thanh toán';

-- Bảng review: Lưu trữ đánh giá
CREATE TABLE review (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    appointment_id CHAR(36) NOT NULL UNIQUE,
    patient_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,
    rating TINYINT UNSIGNED NOT NULL,
    comment TEXT,
    review_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    doctor_response TEXT NULL,
    response_date DATETIME NULL,
    is_visible BOOLEAN DEFAULT TRUE,
    CHECK (rating >= 1 AND rating <= 5),
    FOREIGN KEY (appointment_id) REFERENCES appointment(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
) COMMENT='Lưu trữ thông tin đánh giá';

-- Bảng notification: Lưu thông tin thông báo
CREATE TABLE notification (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,	
    content TEXT NOT NULL,
    type ENUM('APPOINTMENT', 'PAYMENT') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) COMMENT='Lưu trữ thông báo cho người dùng';

-- Bảng invalidated_token: Lưu các JWT token đã bị thu hồi
CREATE TABLE invalidated_token (
    id VARCHAR(36) PRIMARY KEY,
    expiry_time DATETIME NOT NULL
) COMMENT='Lưu trữ các JWT token đã bị thu hồi để kiểm tra khi xác thực';

-- Kiểm tra FK on
SET foreign_key_checks = 1;

-- Trigger để tự động cập nhật average_rating trong bảng doctor
DELIMITER $$
CREATE TRIGGER trg_after_insert_review
AFTER INSERT ON review
FOR EACH ROW
BEGIN
    UPDATE doctor
    SET average_rating = COALESCE((
        SELECT AVG(rating)
        FROM review
        WHERE doctor_id = NEW.doctor_id AND is_visible = TRUE
    ), 0.00)
    WHERE id = NEW.doctor_id;
END $$

CREATE TRIGGER trg_after_update_review
AFTER UPDATE ON review
FOR EACH ROW
BEGIN
    IF OLD.doctor_id != NEW.doctor_id OR OLD.rating != NEW.rating OR OLD.is_visible != NEW.is_visible THEN
        UPDATE doctor
        SET average_rating = COALESCE((
            SELECT AVG(rating)
            FROM review
            WHERE doctor_id = OLD.doctor_id AND is_visible = TRUE
        ), 0.00)
        WHERE id = OLD.doctor_id;
        UPDATE doctor
        SET average_rating = COALESCE((
            SELECT AVG(rating)
            FROM review
            WHERE doctor_id = NEW.doctor_id AND is_visible = TRUE
        ), 0.00)
        WHERE id = NEW.doctor_id;
    END IF;
END $$

CREATE TRIGGER trg_after_delete_review
AFTER DELETE ON review
FOR EACH ROW
BEGIN
    UPDATE doctor
    SET average_rating = COALESCE((
        SELECT AVG(rating)
        FROM review
        WHERE doctor_id = OLD.doctor_id AND is_visible = TRUE
    ), 0.00)
    WHERE id = OLD.doctor_id;
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER before_insert_time_slot
BEFORE INSERT ON time_slot
FOR EACH ROW
BEGIN
    IF NEW.id IS NULL OR NEW.id = '' THEN
        SET NEW.id = UUID();
    END IF;
END $$
DELIMITER ;

-- Procedure tạo tự động slot trống 1 tuần
DELIMITER $$
DROP PROCEDURE IF EXISTS GenerateTimeSlot $$
CREATE PROCEDURE GenerateTimeSlot()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE doc_id CHAR(36);
    DECLARE day ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY');
    DECLARE s_time TIME;
    DECLARE e_time TIME;
    DECLARE cur CURSOR FOR
        SELECT doctor_id, day_of_week, start_time, end_time
        FROM doctor_schedule
        WHERE is_available = TRUE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    SET @today = CURDATE();
    SET @i = 0;
    WHILE @i < 7 DO
        SET @target_date = DATE_ADD(@today, INTERVAL @i DAY);
        SET @target_dow = UPPER(DAYNAME(@target_date));
        OPEN cur;
        read_loop: LOOP
            FETCH cur INTO doc_id, day, s_time, e_time;
            IF done THEN
                LEAVE read_loop;
            END IF;
            IF day = @target_dow THEN
                SET @current_time = s_time;
                WHILE ADDTIME(@current_time, '00:30:00') <= e_time DO
                    SET @slot_start = @current_time;
                    SET @slot_end = ADDTIME(@current_time, '00:30:00');
                    INSERT IGNORE INTO time_slot (doctor_id, slot_date, start_time, end_time)
                    VALUES (doc_id, @target_date, @slot_start, @slot_end);
                    SET @current_time = @slot_end;
                END WHILE;
            END IF;
        END LOOP;
        CLOSE cur;
        SET @i = @i + 1;
        SET done = FALSE;
    END WHILE;
END $$
DELIMITER ;

-- Event để chạy procedure hàng ngày
SET GLOBAL event_scheduler = ON;

CREATE EVENT IF NOT EXISTS ev_generate_slots
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 DAY
DO
    CALL GenerateTimeSlot();

-- === INSERT DỮ LIỆU MẪU ===
-- user
INSERT INTO user (email, username, password, last_name, first_name, phone_number, address, date_of_birth, gender, role) VALUES
('trunghau@admin.com', 'trunghau', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Nguyễn', 'Trung Hậu', '0123456789', '483/11, Đào Sư Tích, Nhà Bè, TP. HCM', '1990-01-01', 'MALE', 'ADMIN'),
('giabao@doctor.com', 'giabao', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Dương', 'Gia Bảo', '0123456788', '12 Đường Sông Nhuệ, P. 3, Q. Tân Bình, TP. HCM', '1985-05-15', 'FEMALE', 'DOCTOR'),
('thithu@doctor.com', 'anhthu', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkoxthi00R0MdPaJPPrf7wO', 'Phạm', 'Thị Thư', '0123456787', '89 Đường Lê Lợi, P. 4, Q. Gò Vấp, TP. HCM', '1990-08-20', 'FEMALE', 'DOCTOR'),
('minhhuy@doctor.com', 'giahuy', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Lý', 'Minh Huy', '0123456786', '45 Đường Hai Bà Trưng, P. Bến Thành, Q. 1, TP. HCM', '1988-12-10', 'MALE', 'DOCTOR'),
('dangquan@patient.com', 'dangquan', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Trần', 'Đăng Quan', '0123456785', '67 Đường Cống Quỳnh, P. Nguyễn Cư Trinh, Q. 1, TP. HCM', '1995-03-10', 'MALE', 'PATIENT'),
('duykhang@patient.com', 'duykhang', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Nguyễn', 'Duy Khang', '0123456784', '23 Đường Võ Văn Tần, P. 6, Q. 3, TP. HCM', '2000-11-25', 'MALE', 'PATIENT');

-- specialty
INSERT INTO specialty (name, description) VALUES
('Tim mạch', 'Chuyên khoa điều trị các bệnh về tim và mạch máu.'),
('Da liễu', 'Chuyên khoa điều trị các bệnh về da, tóc, móng.'),
('Nhi khoa', 'Chuyên khoa chăm sóc sức khỏe cho trẻ em.'),
('Nội tổng quát', 'Chuyên khoa khám và điều trị các bệnh nội khoa thông thường.'),
('Tai Mũi Họng', 'Chuyên khoa điều trị các bệnh về tai, mũi và họng.'),
('Sản phụ khoa', 'Chuyên khoa sản và chăm sóc sức khỏe phụ nữ, thai nhi.'),
('Ngoại tổng quát', 'Chuyên khoa phẫu thuật các bệnh lý cần can thiệp bằng ngoại khoa.'),
('Tiêu hóa – Gan mật – Tụy', 'Chuyên khoa về hệ tiêu hóa, gan mật và tụy.'),
('Thần kinh', 'Chuyên khoa về hệ thần kinh trung ương và ngoại vi.'),
('Thần kinh – Cột sống', 'Chuyên khoa điều trị các bệnh lý thần kinh và cột sống.'),
('Chấn thương chỉnh hình', 'Chuyên khoa về xương khớp, chấn thương và phục hồi chức năng.'),
('Cơ xương khớp', 'Chuyên khoa điều trị bệnh về cơ, khớp, dây chằng.'),
('Mắt', 'Chuyên khoa khám và điều trị các bệnh về mắt.'),
('Răng Hàm Mặt', 'Chuyên khoa nha khoa, chỉnh hình răng hàm mặt.'),
('Y học cổ truyền', 'Chuyên khoa sử dụng phương pháp cổ truyền, thuốc nam, châm cứu, xoa bóp.'),
('Y học dự phòng', 'Chuyên khoa phòng bệnh, dự phòng, kiểm soát dịch bệnh.'),
('Y tế công cộng', 'Chuyên khoa chăm sóc sức khỏe cộng đồng, chính sách y tế.'),
('Gây mê hồi sức', 'Chuyên khoa gây mê, chăm sóc bệnh nhân trước và sau phẫu thuật.'),
('Ung bướu', 'Chuyên khoa chẩn đoán và điều trị các bệnh ung thư.'),
('Hồi sức cấp cứu', 'Chuyên khoa xử trí các trường hợp nguy kịch cấp cứu.');

-- doctor
INSERT INTO doctor (id, specialty_id, years_experience, biography, consultation_fee, average_rating)
SELECT id, (SELECT id FROM specialty WHERE name = 'Tim mạch'), 12, 'Bác sĩ chuyên khoa Tim mạch, kinh nghiệm 12 năm.', 350000.00, 4.00 FROM user WHERE username = 'giabao';
INSERT INTO doctor (id, specialty_id, years_experience, biography, consultation_fee, average_rating)
SELECT id, (SELECT id FROM specialty WHERE name = 'Da liễu'), 7, 'Bác sĩ Da liễu.', 300000.00, 5.00 FROM user WHERE username = 'anhthu';
INSERT INTO doctor (id, specialty_id, years_experience, biography, consultation_fee, average_rating)
SELECT id, (SELECT id FROM specialty WHERE name = 'Tai Mũi Họng'), 8, 'Bác sĩ Tai Mũi Họng, chuyên nội soi.', 320000.00, 3.00 FROM user WHERE username = 'giahuy';

-- doctor_license
INSERT INTO doctor_license (doctor_id, license_number, issuing_authority, issue_date, expiry_date, scope_description, is_verified, verification_date)
SELECT (SELECT id FROM user WHERE username = 'giabao'), 'CCHN-NTMT-54321', 'Bộ Y Tế', '2014-07-01', '2029-07-01', 'Khám Tim mạch', TRUE, CURDATE() FROM user WHERE username = 'trunghau';
INSERT INTO doctor_license (doctor_id, license_number, issuing_authority, issue_date, expiry_date, scope_description, is_verified, verification_date)
SELECT (SELECT id FROM user WHERE username = 'anhthu'), 'CCHN-BAT-09876', 'Sở Y Tế TP.HCM', '2019-02-15', '2029-02-15', 'Khám Da liễu', FALSE, NULL FROM user WHERE username = 'trunghau';
INSERT INTO doctor_license (doctor_id, license_number, issuing_authority, issue_date, expiry_date, scope_description, is_verified, verification_date)
SELECT (SELECT id FROM user WHERE username = 'giahuy'), 'CCHN-GH-11223', 'Sở Y Tế TP.HCM', '2018-10-20', '2028-10-20', 'Khám Tai Mũi Họng', FALSE, NULL FROM user WHERE username = 'trunghau';

-- patient
INSERT INTO patient (id, medical_history)
SELECT id, 'Khỏe mạnh, không dị ứng.' FROM user WHERE username = 'dangquan';
INSERT INTO patient (id, medical_history)
SELECT id, 'Viêm mũi dị ứng.' FROM user WHERE username = 'duykhang';

-- doctor_schedule
SET @today = CURDATE();
SET @next_tuesday = DATE_ADD(@today, INTERVAL (9 - DAYOFWEEK(@today)) % 7 DAY);
SET @next_thursday = DATE_ADD(@today, INTERVAL (11 - DAYOFWEEK(@today)) % 7 DAY);
SET @next_monday = DATE_ADD(@today, INTERVAL (8 - DAYOFWEEK(@today)) % 7 DAY);
SET @next_wednesday = DATE_ADD(@today, INTERVAL (10 - DAYOFWEEK(@today)) % 7 DAY);
SET @next_friday = DATE_ADD(@today, INTERVAL (12 - DAYOFWEEK(@today)) % 7 DAY);

INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time)
SELECT (SELECT id FROM user WHERE username = 'giabao'), 'TUESDAY', '08:00:00', '12:00:00' FROM user WHERE username = 'giabao';
INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time)
SELECT (SELECT id FROM user WHERE username = 'giabao'), 'THURSDAY', '13:30:00', '17:00:00' FROM user WHERE username = 'giabao';
INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time)
SELECT (SELECT id FROM user WHERE username = 'anhthu'), 'MONDAY', '09:00:00', '11:30:00' FROM user WHERE username = 'anhthu';
INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time)
SELECT (SELECT id FROM user WHERE username = 'anhthu'), 'WEDNESDAY', '14:00:00', '16:30:00' FROM user WHERE username = 'anhthu';
INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time)
SELECT (SELECT id FROM user WHERE username = 'anhthu'), 'FRIDAY', '08:30:00', '11:00:00' FROM user WHERE username = 'anhthu';
INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time)
SELECT (SELECT id FROM user WHERE username = 'giahuy'), 'MONDAY', '13:00:00', '17:00:00' FROM user WHERE username = 'giahuy';
INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time)
SELECT (SELECT id FROM user WHERE username = 'giahuy'), 'THURSDAY', '08:00:00', '11:30:00' FROM user WHERE username = 'giahuy';

-- appointment
INSERT INTO appointment (patient_id, doctor_id, appointment_time, reason, status, consultation_type)
SELECT (SELECT id FROM user WHERE username = 'dangquan'), (SELECT id FROM user WHERE username = 'giabao'), CONCAT(@next_tuesday, ' 09:00:00'), 'Kiểm tra tim', 'SCHEDULED', 'OFFLINE' FROM user WHERE username = 'dangquan';
SET @appt1_id = (SELECT id FROM appointment WHERE reason = 'Kiểm tra tim' AND appointment_time = CONCAT(@next_tuesday, ' 09:00:00'));
INSERT INTO appointment (patient_id, doctor_id, appointment_time, reason, status, consultation_type, video_call_link)
SELECT (SELECT id FROM user WHERE username = 'duykhang'), (SELECT id FROM user WHERE username = 'anhthu'), DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY), 'Tư vấn dị ứng', 'COMPLETED', 'ONLINE', 'https://meet.example.com/khang-thu-123' FROM user WHERE username = 'duykhang';
SET @appt2_id = (SELECT id FROM appointment WHERE reason = 'Tư vấn dị ứng' AND appointment_time = DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY));
INSERT INTO appointment (patient_id, doctor_id, appointment_time, reason, status, consultation_type)
SELECT (SELECT id FROM user WHERE username = 'dangquan'), (SELECT id FROM user WHERE username = 'giahuy'), CONCAT(@next_thursday, ' 10:00:00'), 'Khám TMH', 'SCHEDULED', 'OFFLINE' FROM user WHERE username = 'dangquan';
SET @appt3_id = (SELECT id FROM appointment WHERE reason = 'Khám TMH' AND appointment_time = CONCAT(@next_thursday, ' 10:00:00'));
INSERT INTO appointment (patient_id, doctor_id, appointment_time, reason, status, cancellation_reason, consultation_type)
SELECT (SELECT id FROM user WHERE username = 'duykhang'), (SELECT id FROM user WHERE username = 'anhthu'), DATE_SUB(CONCAT(@next_friday, ' 09:30:00'), INTERVAL 7 DAY), 'Tái khám da', 'CANCELLED_BY_PATIENT', 'Bận đột xuất', 'OFFLINE' FROM user WHERE username = 'duykhang';
SET @appt4_id = (SELECT id FROM appointment WHERE reason = 'Tái khám da' AND appointment_time = DATE_SUB(CONCAT(@next_friday, ' 09:30:00'), INTERVAL 7 DAY));
INSERT INTO appointment (patient_id, doctor_id, appointment_time, reason, status, consultation_type, video_call_link)
SELECT (SELECT id FROM user WHERE username = 'hoale'), (SELECT id FROM user WHERE username = 'anhthu'), CONCAT(@next_monday, ' 10:00:00'), 'Tư vấn da', 'SCHEDULED', 'ONLINE', 'https://meet.example.com/hoa-thu-456' FROM user WHERE username = 'hoale';
SET @appt5_id = (SELECT id FROM appointment WHERE reason = 'Tư vấn da' AND appointment_time = CONCAT(@next_monday, ' 10:00:00'));

-- medicine
INSERT INTO medicine (name, description, unit, price_per_unit) VALUES
-- Thuốc hạ sốt, giảm đau
('Paracetamol', 'Hạ sốt, giảm đau nhẹ.', 'Viên', 500),
('Ibuprofen', 'Giảm đau, kháng viêm, hạ sốt.', 'Viên', 1000),
('Aspirin', 'Giảm đau, hạ sốt, chống viêm, chống kết tập tiểu cầu.', 'Viên', 1200),

-- Thuốc kháng sinh
('Amoxicillin', 'Kháng sinh điều trị nhiễm khuẩn.', 'Viên', 2000),
('Azithromycin', 'Kháng sinh nhóm macrolid, điều trị nhiễm khuẩn hô hấp.', 'Viên', 3500),
('Cefuroxime', 'Kháng sinh cephalosporin, điều trị viêm phổi, viêm tai.', 'Viên', 4000),

-- Thuốc dị ứng, hô hấp
('Loratadin', 'Kháng histamine, điều trị dị ứng.', 'Viên', 1000),
('Cetirizine', 'Kháng dị ứng, giảm ngứa.', 'Viên', 800),
('Fluticasone', 'Xịt mũi, điều trị viêm mũi dị ứng.', 'Ống xịt', 5000),
('Salbutamol', 'Giãn phế quản, điều trị hen suyễn.', 'Ống hít', 6000),

-- Thuốc tim mạch, huyết áp
('Amlodipine', 'Điều trị tăng huyết áp, đau thắt ngực.', 'Viên', 1500),
('Enalapril', 'Điều trị tăng huyết áp, suy tim.', 'Viên', 2000),
('Atorvastatin', 'Hạ mỡ máu, phòng ngừa biến chứng tim mạch.', 'Viên', 2500),

-- Thuốc tiêu hóa
('Omeprazole', 'Điều trị viêm loét dạ dày, trào ngược dạ dày-thực quản.', 'Viên', 1200),
('Esomeprazole', 'Thuốc ức chế tiết acid, điều trị GERD.', 'Viên', 3000),
('Metronidazole', 'Điều trị nhiễm trùng đường ruột, âm đạo.', 'Viên', 1800),

-- Vitamin và bổ sung
('Vitamin C', 'Tăng sức đề kháng, chống oxy hóa.', 'Viên', 700),
('Vitamin D3', 'Hỗ trợ xương, tăng hấp thu canxi.', 'Viên', 1000),
('Canxi Carbonate', 'Bổ sung canxi, phòng loãng xương.', 'Viên', 1200),

-- Giảm đau mạnh, kê đơn
('Diclofenac', 'Giảm đau, chống viêm trong viêm khớp, đau cơ.', 'Viên', 1500),
('Morphin', 'Giảm đau gây nghiện, dùng trong đau nặng.', 'Ống tiêm', 20000);


-- health_record
INSERT INTO health_record (patient_id, appointment_id, user_id, record_date, symptoms, diagnosis, notes)
SELECT (SELECT id FROM user WHERE username = 'duykhang'), @appt2_id, (SELECT id FROM user WHERE username = 'anhthu'), DATE(DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY)), 'Nghẹt mũi', 'Viêm mũi dị ứng', 'Tránh dị nguyên.' FROM user WHERE username = 'duykhang';
SET @record1_id = (SELECT id FROM health_record WHERE appointment_id = @appt2_id);
INSERT INTO health_record (patient_id, appointment_id, user_id, record_date, symptoms, notes)
SELECT (SELECT id FROM user WHERE username = 'dangquan'), NULL, (SELECT id FROM user WHERE username = 'dangquan'), DATE_SUB(@today, INTERVAL 3 DAY), 'Đau đầu', 'BN tự ghi, nghi do stress.' FROM user WHERE username = 'dangquan';
SET @record2_id = (SELECT id FROM health_record WHERE symptoms = 'Đau đầu' AND appointment_id IS NULL);

-- prescription
INSERT INTO prescription (health_record_id, doctor_id, patient_id, issue_date, notes)
SELECT @record1_id, (SELECT id FROM user WHERE username = 'anhthu'), (SELECT id FROM user WHERE username = 'duykhang'), DATE(DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY)), 'Uống sau bữa ăn.' FROM user WHERE username = 'duykhang';
SET @prescription1_id = (SELECT id FROM prescription WHERE health_record_id = @record1_id);
INSERT INTO prescription (health_record_id, doctor_id, patient_id, issue_date, notes)
SELECT @record2_id, (SELECT id FROM user WHERE username = 'giabao'), (SELECT id FROM user WHERE username = 'dangquan'), DATE(DATE_SUB(CONCAT(@next_monday, ' 14:30:00'), INTERVAL 3 DAY)), 'Uống sáng, theo dõi huyết áp.' FROM user WHERE username = 'dangquan';
SET @prescription2_id = (SELECT id FROM prescription WHERE health_record_id = @record2_id);

-- medicine_prescription
INSERT INTO medicine_prescription (prescription_id, medicine_id, quantity, dosage_instruction)
SELECT @prescription1_id, (SELECT id FROM medicine WHERE name = 'Loratadin'), 10, '1 viên/ngày, uống buổi tối.' FROM medicine WHERE name = 'Loratadin';
INSERT INTO medicine_prescription (prescription_id, medicine_id, quantity, dosage_instruction)
SELECT @prescription1_id, (SELECT id FROM medicine WHERE name = 'Fluticasone'), 30, 'Xịt mỗi bên mũi 1 lần/ngày.' FROM medicine WHERE name = 'Fluticasone';
INSERT INTO medicine_prescription (prescription_id, medicine_id, quantity, dosage_instruction)
SELECT @prescription2_id, (SELECT id FROM medicine WHERE name = 'Amlodipine'), 30, '1 viên/ngày, uống buổi sáng.' FROM medicine WHERE name = 'Amlodipine';

-- invoice
INSERT INTO invoice (appointment_id, amount, due_date, status)
SELECT @appt1_id, 350000.00, DATE_ADD(@next_tuesday, INTERVAL 7 DAY), 'PENDING' FROM appointment WHERE id = @appt1_id;
INSERT INTO invoice (appointment_id, amount, issue_date, status)
SELECT @appt2_id, 300000.00, DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY), 'PAID' FROM appointment WHERE id = @appt2_id;
INSERT INTO invoice (appointment_id, amount, due_date, status)
SELECT @appt3_id, 320000.00, DATE_ADD(@next_thursday, INTERVAL 7 DAY), 'PENDING' FROM appointment WHERE id = @appt3_id;
INSERT INTO invoice (appointment_id, amount, due_date, status)
SELECT @appt5_id, 300000.00, DATE_ADD(@next_monday, INTERVAL 7 DAY), 'PENDING' FROM appointment WHERE id = @appt5_id;

-- payment
INSERT INTO payment (invoice_id, amount_paid, payment_method, transaction_id, status, payment_date, notes)
SELECT (SELECT id FROM invoice WHERE appointment_id = @appt2_id), 300000.00, 'VNPAY', 'VNPAY_1650012345', 'COMPLETED', DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY), 'Thanh toán online.' FROM invoice WHERE appointment_id = @appt2_id;

-- review
INSERT INTO review (appointment_id, patient_id, doctor_id, rating, comment, review_date, doctor_response, response_date, is_visible)
SELECT @appt2_id, (SELECT id FROM user WHERE username = 'duykhang'), (SELECT id FROM user WHERE username = 'anhthu'), 5, 'BS Thư tư vấn online nhiệt tình.', DATE_ADD(DATE(DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY)), INTERVAL 1 DAY), 'Cảm ơn bạn!', DATE_ADD(DATE(DATE_SUB(CONCAT(@next_wednesday, ' 14:30:00'), INTERVAL 7 DAY)), INTERVAL 2 DAY), TRUE FROM user WHERE username = 'duykhang';

-- notification
INSERT INTO notification (user_id, title, content, type, is_read)
SELECT (SELECT id FROM user WHERE username = 'dangquan'), 'Nhắc nhở lịch hẹn', 'Bạn có lịch hẹn với BS Gia Bao vào 09:00 ngày 2025-08-05.', 'APPOINTMENT', FALSE FROM user WHERE username = 'dangquan';
INSERT INTO notification (user_id, title, content, type, is_read)
SELECT (SELECT id FROM user WHERE username = 'duykhang'), 'Xác nhận hủy lịch', 'Lịch hẹn của bạn vào 09:30 ngày 2025-08-01 đã được hủy.', 'APPOINTMENT', TRUE FROM user WHERE username = 'duykhang';
INSERT INTO notification (user_id, title, content, type, is_read)
SELECT (SELECT id FROM user WHERE username = 'giabao'), 'Lịch hẹn mới', 'Bệnh nhân Đăng Quan đã đặt lịch hẹn vào 09:00 ngày 2025-08-05.', 'APPOINTMENT', FALSE FROM user WHERE username = 'giabao';