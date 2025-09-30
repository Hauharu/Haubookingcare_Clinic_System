package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentStatusUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.AppointmentResponse;
import com.trunghau1510.Clinic_Management.entity.Appointment;
import com.trunghau1510.Clinic_Management.entity.Doctor;
import com.trunghau1510.Clinic_Management.entity.Patient;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.TimeSlot;
import com.trunghau1510.Clinic_Management.entity.enums.Status;
import com.trunghau1510.Clinic_Management.mapper.AppointmentMapper;
import com.trunghau1510.Clinic_Management.repository.*;
import com.trunghau1510.Clinic_Management.service.AppointmentService;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;
import com.trunghau1510.Clinic_Management.service.NotificationService;
import com.trunghau1510.Clinic_Management.service.EmailService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentServiceImplement implements AppointmentService {

    AppointmentRepository appointmentRepository;
    DoctorRepository doctorRepository;
    TimeSlotRepository timeSlotRepository;
    PatientRepository patientRepository;
    AppointmentMapper appointmentMapper;
    PaginationConfig paginationConfig;
    UserRepository userRepository;
    NotificationService notificationService;
    EmailService emailService;

    // Hàm private truy vấn lịch hẹn theo điều kiện
    private List<Appointment> queryAppointments(String patientId, String doctorId, Pageable pageable) {
        if (patientId != null && doctorId != null) {
            return appointmentRepository.findByPatient_IdAndDoctor_Id(patientId, doctorId, pageable);
        } else if (patientId != null) {
            return appointmentRepository.findByPatient_Id(patientId, pageable);
        } else if (doctorId != null) {
            return appointmentRepository.findByDoctor_Id(doctorId, pageable);
        } else {
            return appointmentRepository.findAll(pageable).getContent();
        }
    }

    // Hàm private gửi notification và email xác nhận lịch khám
    private void sendAppointmentNotifications(Patient patient, Doctor doctor, LocalDate slotDate, LocalTime startTime, boolean isUpdate) {
        if (patient.getUser() == null) {
            System.out.println("[ERROR] patient.getUser() is null! Không thể tạo notification cho bệnh nhân.");
            return;
        }
        String title = isUpdate ? "Cập nhật lịch khám" : "Đặt lịch khám thành công";
        String message = isUpdate ? "Lịch khám của bạn đã được cập nhật. Vui lòng kiểm tra lại thông tin." :
                "Bạn đã đặt lịch khám với bác sĩ " + doctor.getUser().getFirstName() + " vào " + slotDate + " lúc " + startTime;
        notificationService.createNotification(
            patient.getUser(),
            title,
            message
        );
        // Gửi email xác nhận cho bệnh nhân
        emailService.sendAppointmentConfirmation(
            patient.getUser().getEmail(),
            isUpdate ? "Cập nhật lịch khám" : "Xác nhận đặt lịch khám",
            patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
            doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName(),
            slotDate + " " + startTime
        );
        // Gửi email xác nhận cho bác sĩ
        emailService.sendAppointmentConfirmationtoDoctor(
            doctor.getUser().getEmail(),
            "Thông báo có bệnh nhân đặt lịch khám",
            patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
            doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName(),
            slotDate + " " + startTime
        );
    }

    @Override
    public List<AppointmentResponse> getAppointments(String patientId, String doctorId, Integer page, Integer size) {
        int pageNumber = (page != null) ? page : paginationConfig.getDefaultPage();
        int pageSize = (size != null) ? size : paginationConfig.getDefaultSize();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Appointment> appointments = queryAppointments(patientId, doctorId, pageable);
        return appointments.stream().map(appointmentMapper::toAppointmentResponse).collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse registerAppointment(AppointmentCreationRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_EXISTS));
        LocalDate slotDate = request.getAppointmentTime().toLocalDate();
        LocalTime startTime = request.getAppointmentTime().toLocalTime();
        // Kiểm tra slot có trống không
        TimeSlot slot = timeSlotRepository
            .findByDoctor_IdAndSlotDateAndStartTimeAndIsBooked(doctor.getId(), slotDate, startTime, false)
            .orElseThrow(() -> new AppException(ErrorCode.TIMESLOT_NOT_AVAILABLE));
        // Đặt slot thành đã đặt
        slot.setIsBooked(true);
        timeSlotRepository.save(slot);
        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(Status.SCHEDULED);
        appointmentRepository.save(appointment);
        sendAppointmentNotifications(patient, doctor, slotDate, startTime, false);
        return appointmentMapper.toAppointmentResponse(appointment);
    }

    @Override
    public AppointmentResponse updateAppointment(String id, AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        // Nếu đổi thời gian khám
        if (request.getAppointmentTime() != null && !request.getAppointmentTime().equals(appointment.getAppointmentTime())) {
            LocalDate oldDate = appointment.getAppointmentTime().toLocalDate();
            LocalTime oldTime = appointment.getAppointmentTime().toLocalTime();
            LocalDate newDate = request.getAppointmentTime().toLocalDate();
            LocalTime newTime = request.getAppointmentTime().toLocalTime();
            String doctorId = appointment.getDoctor().getId();
            // Slot cũ: cập nhật về chưa đặt
            TimeSlot oldSlot = timeSlotRepository
                .findByDoctor_IdAndSlotDateAndStartTimeAndIsBooked(doctorId, oldDate, oldTime, true)
                .orElse(null);
            if (oldSlot != null) {
                oldSlot.setIsBooked(false);
                timeSlotRepository.save(oldSlot);
            }
            // Slot mới: kiểm tra trống, cập nhật thành đã đặt
            TimeSlot newSlot = timeSlotRepository
                .findByDoctor_IdAndSlotDateAndStartTimeAndIsBooked(doctorId, newDate, newTime, false)
                .orElseThrow(() -> new AppException(ErrorCode.TIMESLOT_NOT_AVAILABLE));
            newSlot.setIsBooked(true);
            timeSlotRepository.save(newSlot);
            appointment.setAppointmentTime(request.getAppointmentTime());
        }
        appointmentMapper.updateEntity(appointment, request);
        appointmentRepository.save(appointment);
        sendAppointmentNotifications(appointment.getPatient(), appointment.getDoctor(), appointment.getAppointmentTime().toLocalDate(), appointment.getAppointmentTime().toLocalTime(), true);
        return appointmentMapper.toAppointmentResponse(appointment);
    }

    @Override
    public AppointmentResponse updateStatus(String id, AppointmentStatusUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        if (request.getStatus() == Status.CANCELLED_BY_PATIENT) {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
            if (currentUser == null || !currentUser.getId().equals(appointment.getPatient().getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
        // Cập nhật trạng thái lịch hẹn
        appointment.setStatus(request.getStatus());
        appointmentRepository.save(appointment);
        String statusMsg = "Trạng thái lịch khám của bạn đã được cập nhật: " + request.getStatus();
        notificationService.createNotification(
                appointment.getPatient().getUser(),
                "Cập nhật trạng thái lịch khám",
                statusMsg
        );
        return appointmentMapper.toAppointmentResponse(appointment);
    }

    @Override
    public void deleteAppointment(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        appointment.setStatus(Status.CANCELLED_BY_PATIENT);
        appointmentRepository.save(appointment);
        // Mở lại slot isBooked=false
        LocalDate slotDate = appointment.getAppointmentTime().toLocalDate();
        LocalTime startTime = appointment.getAppointmentTime().toLocalTime();
        String doctorId = appointment.getDoctor().getId();
        TimeSlot slot = timeSlotRepository
                .findByDoctor_IdAndSlotDateAndStartTime(doctorId, slotDate, startTime)
                .orElse(null);
        if (slot != null) {
            slot.setIsBooked(false);
            timeSlotRepository.save(slot);
        }
        // Gửi notification và email hủy lịch cho bệnh nhân và bác sĩ
        notificationService.createNotification(
                appointment.getPatient().getUser(),
                "Lịch khám đã bị hủy",
                "Lịch khám của bạn đã bị hủy."
        );
        emailService.sendAppointmentCancellation(
            appointment.getPatient().getUser().getEmail(),
            "Thông báo hủy lịch khám",
            appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName(),
            appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName(),
            slotDate + " " + startTime
        );
        emailService.sendAppointmentCancellationToDoctor(
            appointment.getDoctor().getUser().getEmail(),
            "Thông báo bệnh nhân hủy lịch khám",
            appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName(),
            appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName(),
            slotDate + " " + startTime
        );
    }

    @Override
    public AppointmentResponse getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        return appointmentMapper.toAppointmentResponse(appointment);
    }

    @Override
    public Appointment getAppointmentEntityById(String id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_EXISTS));
    }
}
