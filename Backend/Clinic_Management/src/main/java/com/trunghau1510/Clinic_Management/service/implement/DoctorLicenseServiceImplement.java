package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorLicenseResponse;
import com.trunghau1510.Clinic_Management.entity.Doctor;
import com.trunghau1510.Clinic_Management.entity.DoctorLicense;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.DoctorLicenseMapper;
import com.trunghau1510.Clinic_Management.repository.DoctorLicenseRepository;
import com.trunghau1510.Clinic_Management.repository.DoctorRepository;
import com.trunghau1510.Clinic_Management.repository.UserRepository;
import com.trunghau1510.Clinic_Management.service.DoctorLicenseService;
import com.trunghau1510.Clinic_Management.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DoctorLicenseServiceImplement implements DoctorLicenseService {

    DoctorLicenseRepository doctorLicenseRepository;
    DoctorRepository doctorRepository;
    DoctorLicenseMapper doctorLicenseMapper;
    UserRepository userRepository;
    EmailService emailService;

    @Override
    @Transactional
    public DoctorLicenseResponse createLicense(DoctorLicenseCreationRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_EXISTED));
        if (doctorLicenseRepository.findByDoctorId(request.getDoctorId()).isPresent()) {
            throw new AppException(ErrorCode.DOCTOR_LICENSE_EXISTED);
        }
        if (doctorLicenseRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new AppException(ErrorCode.LICENSE_NUMBER_EXISTED);
        }
        // Tạo license mới cho bác sĩ
        DoctorLicense license = doctorLicenseMapper.toDoctorLicense(request);
        license.setDoctor(doctor);
        license.setIsVerified(false); // License mặc định chưa xác thực
        DoctorLicense savedLicense = doctorLicenseRepository.save(license);
        return doctorLicenseMapper.toDoctorLicenseResponse(savedLicense);
    }

    @Override
    @Transactional
    public DoctorLicenseResponse updateLicense(String id, DoctorLicenseUpdateRequest request) {
        DoctorLicense license = doctorLicenseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_LICENSE_NOT_EXISTED));
        if (request.getLicenseNumber() != null &&
                !request.getLicenseNumber().equals(license.getLicenseNumber())) {
            if (doctorLicenseRepository.existsByLicenseNumberAndIdNot(request.getLicenseNumber(), id)) {
                throw new AppException(ErrorCode.LICENSE_NUMBER_EXISTED);
            }
        }
        if (request.getIsVerified() != null && request.getIsVerified() &&
                (license.getIsVerified() == null || !license.getIsVerified())) {
            request.setVerificationDate(LocalDate.now());
        }
        doctorLicenseMapper.updateDoctorLicense(license, request);
        DoctorLicense updatedLicense = doctorLicenseRepository.save(license);
        return doctorLicenseMapper.toDoctorLicenseResponse(updatedLicense);
    }

    @Override
    @Transactional
    public void deleteLicense(String id) {
        if (!doctorLicenseRepository.existsById(id)) {
            throw new AppException(ErrorCode.DOCTOR_LICENSE_NOT_EXISTED);
        }
        doctorLicenseRepository.deleteById(id);
    }

    public DoctorLicenseResponse getLicenseByDoctorId(String doctorId) {
        return doctorLicenseRepository.findByDoctorId(doctorId)
                .map(doctorLicenseMapper::toDoctorLicenseResponse)
                .orElse(null);
    }

    @Override
    public List<DoctorLicenseResponse> getAllUnverifiedLicenses() {
        List<DoctorLicense> licenses = doctorLicenseRepository.findAll()
            .stream()
            .filter(l -> l.getIsVerified() == null || !l.getIsVerified())
            .toList();
        return licenses.stream()
                .map(doctorLicenseMapper::toDoctorLicenseResponse)
                .toList();
    }

    @Override
    public List<DoctorLicenseResponse> getAllVerifiedLicenses() {
        List<DoctorLicense> licenses = doctorLicenseRepository.findAll()
            .stream()
            .filter(l -> l.getIsVerified() != null && l.getIsVerified())
            .toList();
        return licenses.stream()
                .map(doctorLicenseMapper::toDoctorLicenseResponse)
                .toList();
    }

    @Override
    public List<DoctorLicenseResponse> getAllLicenses() {
        List<DoctorLicense> licenses = doctorLicenseRepository.findAll();
        return licenses.stream()
                .map(doctorLicenseMapper::toDoctorLicenseResponse)
                .toList();
    }

    @Override
    @Transactional
    public void approveLicense(String id, boolean isApproved) {
        DoctorLicense license = doctorLicenseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_LICENSE_NOT_EXISTED));
        if (isApproved) {
            license.setIsVerified(true);
            license.setVerificationDate(LocalDate.now());
            doctorLicenseRepository.save(license);
            // Nếu user đang là PATIENT thì chuyển thành DOCTOR
            User user = license.getDoctor().getUser();
            if (user.getRole() == Role.PATIENT) {
                user.setRole(Role.DOCTOR);
                userRepository.save(user);
                try {
                    emailService.sendDoctorApprovalEmail(user);
                } catch (Exception e) {
                    // Không throw exception để không ảnh hưởng đến việc approve license
                }
            }
        } else {
            doctorLicenseRepository.delete(license);
            User user = license.getDoctor().getUser();
            try {
                emailService.sendDoctorRejectionEmail(user);
            } catch (Exception e) {
                // Không throw exception để không ảnh hưởng đến việc reject license
            }
        }
    }
}
