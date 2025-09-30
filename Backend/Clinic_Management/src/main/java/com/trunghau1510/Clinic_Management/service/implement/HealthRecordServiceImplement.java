package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.request.HealthRecordCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.HealthRecordUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.AppointmentResponse;
import com.trunghau1510.Clinic_Management.dto.response.HealthRecordResponse;
import com.trunghau1510.Clinic_Management.dto.response.UserResponse;
import com.trunghau1510.Clinic_Management.entity.Appointment;
import com.trunghau1510.Clinic_Management.entity.HealthRecord;
import com.trunghau1510.Clinic_Management.entity.Patient;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.HealthRecordMapper;
import com.trunghau1510.Clinic_Management.repository.HealthRecordRepository;
import com.trunghau1510.Clinic_Management.service.AppointmentService;
import com.trunghau1510.Clinic_Management.service.HealthRecordService;
import com.trunghau1510.Clinic_Management.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthRecordServiceImplement implements HealthRecordService {

    HealthRecordRepository healthRecordRepository;
    HealthRecordMapper healthRecordMapper;
    UserService userService;
    AppointmentService appointmentService;

    @Override
    @Transactional
    public HealthRecordResponse addHealthRecord(HealthRecordCreationRequest request) {
        UserResponse userResponse = userService.getUserById(request.getPatientId());
        User patientUser = userService.getUserByUsername(userResponse.getUsername());

        if (patientUser.getRole() != Role.PATIENT) {
            throw new AppException(ErrorCode.PATIENT_NOT_EXISTS);
        }

        Patient patient = new Patient();
        patient.setId(patientUser.getId());
        patient.setUser(patientUser);

        if (request.getAppointmentId() != null) {
            try {
                AppointmentResponse appointmentResponse = appointmentService.getAppointmentById(request.getAppointmentId());
                if (!appointmentResponse.getPatientId().equals(patient.getId())) {
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }
            } catch (Exception e) {
                throw new AppException(ErrorCode.USER_NOT_EXISTS);
            }
        }

        HealthRecord healthRecord = healthRecordMapper.toHealthRecord(request);
        healthRecord.setPatient(patient);

        if (request.getAppointmentId() != null) {
            Appointment appointment = appointmentService.getAppointmentEntityById(request.getAppointmentId());
            healthRecord.setAppointment(appointment);
        }
        // Ghi nhận bác sĩ tạo hồ sơ
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentUsername != null && !currentUsername.equals("anonymousUser")) {
            try {
                User currentUser = userService.getUserByUsername(currentUsername);
                if (currentUser != null) {
                    healthRecord.setUser(currentUser);
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        if (healthRecord.getRecordDate() == null) {
            healthRecord.setRecordDate(LocalDate.now());
        }

        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);
        return healthRecordMapper.toHealthRecordResponse(savedHealthRecord);
    }

    @Override
    @Transactional
    public HealthRecordResponse updateHealthRecord(String healthRecordId, HealthRecordUpdateRequest request) {
        HealthRecord healthRecord = healthRecordRepository.findById(healthRecordId)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_EXISTS));

        healthRecordMapper.updateHealthRecord(healthRecord, request);
        HealthRecord updatedHealthRecord = healthRecordRepository.save(healthRecord);
        return healthRecordMapper.toHealthRecordResponse(updatedHealthRecord);
    }

    @Override
    public HealthRecordResponse getHealthRecordByAppointmentId(String appointmentId) {
        HealthRecord healthRecord = healthRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_EXISTS));
        return healthRecordMapper.toHealthRecordResponse(healthRecord);
    }

    @Override
    public List<HealthRecordResponse> getHealthRecordsByUserId(String userId) {
        UserResponse userResponse = userService.getUserById(userId);
        User user = userService.getUserByUsername(userResponse.getUsername());

        List<HealthRecord> healthRecords;
        if (user.getPatient() != null) {
            healthRecords = healthRecordRepository.findByPatientIdWithAppointment(user.getId());
        } else {
            healthRecords = healthRecordRepository.findByUser(user);
        }

        return healthRecords.stream()
                .map(healthRecordMapper::toHealthRecordResponse)
                .toList();
    }
}