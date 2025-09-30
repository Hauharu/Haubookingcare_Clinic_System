package com.trunghau1510.Clinic_Management.service.implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trunghau1510.Clinic_Management.dto.request.DoctorSearchRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.DoctorResponse;
import com.trunghau1510.Clinic_Management.entity.Doctor;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.DoctorMapper;
import com.trunghau1510.Clinic_Management.repository.DoctorLicenseRepository;
import com.trunghau1510.Clinic_Management.repository.DoctorRepository;
import com.trunghau1510.Clinic_Management.repository.SpecialtyRepository;
import com.trunghau1510.Clinic_Management.repository.UserRepository;
import com.trunghau1510.Clinic_Management.service.DoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DoctorServiceImplement implements DoctorService {

    Cloudinary cloudinary;
    DoctorRepository doctorRepository;
    UserRepository userRepository;
    SpecialtyRepository specialtyRepository;
    DoctorLicenseRepository doctorLicenseRepository;
    DoctorMapper doctorMapper;


    @Override
    @Transactional
    public void deleteDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        User user = doctor.getUser();

        doctorLicenseRepository.deleteByDoctorId(id);
        doctorRepository.delete(doctor);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DoctorResponse updateDoctor(String id, DoctorUpdateRequest request) {
        try {
            Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
            User user = doctor.getUser();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAddress(request.getAddress());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setGender(request.getGender());
            MultipartFile avatarFile = request.getAvatar();
            if (avatarFile != null && !avatarFile.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(avatarFile.getBytes(), ObjectUtils.emptyMap());
                String avatarUrl = (String) uploadResult.get("secure_url");
                user.setAvatar(avatarUrl);
            }
            // Không cập nhật username và password!
            doctor.setYearsExperience(request.getYearsExperience());
            doctor.setBiography(request.getBiography());
            doctor.setConsultationFee(request.getConsultationFee());
            if (request.getSpecialtyId() != null) {
                doctor.setSpecialty(specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND)));
            }
            doctorRepository.save(doctor);
            userRepository.save(user);
            return doctorMapper.toDoctorResponse(doctor);
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    @Override
    public Page<DoctorResponse> getDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable)
                .map(doctorMapper::toDoctorResponse);
    }

    @Override
    public DoctorResponse getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        return doctorMapper.toDoctorResponse(doctor);
    }

    @Override
    public Page<DoctorResponse> searchDoctorsByKeyword(DoctorSearchRequest request, Pageable pageable) {
        String keyword = request.getKeyword();
        System.out.println("[DoctorSearch] Keyword received: " + keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            Page<Doctor> doctors = doctorRepository.findAll(pageable);
            System.out.println("[DoctorSearch] Total doctors found (all): " + doctors.getTotalElements());
            return doctors.map(doctorMapper::toDoctorResponse);
        }
        Page<Doctor> doctors = doctorRepository.searchByKeyword(keyword, pageable);
        System.out.println("[DoctorSearch] Total doctors found (by keyword): " + doctors.getTotalElements());
        return doctors.map(doctorMapper::toDoctorResponse);
    }
}