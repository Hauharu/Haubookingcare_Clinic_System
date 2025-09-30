package com.trunghau1510.Clinic_Management.service.implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trunghau1510.Clinic_Management.dto.request.DoctorCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.UserCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.UserUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.UserResponse;
import com.trunghau1510.Clinic_Management.entity.Doctor;
import com.trunghau1510.Clinic_Management.entity.Patient;
import com.trunghau1510.Clinic_Management.entity.Specialty;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.DoctorMapper;
import com.trunghau1510.Clinic_Management.mapper.UserMapper;
import com.trunghau1510.Clinic_Management.repository.DoctorRepository;
import com.trunghau1510.Clinic_Management.repository.PatientRepository;
import com.trunghau1510.Clinic_Management.repository.SpecialtyRepository;
import com.trunghau1510.Clinic_Management.repository.UserRepository;
import com.trunghau1510.Clinic_Management.service.EmailService;
import com.trunghau1510.Clinic_Management.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplement implements UserService {

    UserRepository userRepository;
    DoctorRepository doctorRepository;
    SpecialtyRepository specialtyRepository;
    UserMapper userMapper;
    DoctorMapper doctorMapper;
    PasswordEncoder passwordEncoder;
    Cloudinary cloudinary;
    EmailService emailService;
    PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Override
    public UserResponse createUser(UserCreationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.ENTITY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.ENTITY_EXISTS);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.ENTITY_EXISTS);
        }

        if (request.getRole() == null) {
            request.setRole(Role.PATIENT);
        }

        User user = userMapper.toUser(request);
        user.setRole(request.getRole());

        if (request.getRole() == Role.DOCTOR) {
            String randomPassword = UUID.randomUUID().toString().substring(0, 8);
            user.setPassword(passwordEncoder.encode(randomPassword));
            user.setIsActive(true);

            uploadAvatar(user, request.getAvatar());
            user = userRepository.save(user);
            userRepository.flush();

            if (request instanceof DoctorCreationRequest doctorRequest) {
                Doctor doctor = doctorMapper.toDoctor(doctorRequest);
                doctor.setUser(user);
                if (doctorRequest.getSpecialtyId() != null) {
                    Specialty specialty = specialtyRepository.findById(doctorRequest.getSpecialtyId())
                            .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
                    doctor.setSpecialty(specialty);
                }
                doctor.setAverageRating(BigDecimal.valueOf(0.0));
                doctorRepository.save(doctor);
            }
            emailService.sendDoctorAccountCreatedEmail(user, user.getUsername(), randomPassword);
        } else {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setIsActive(true);
            uploadAvatar(user, request.getAvatar());
            user = userRepository.save(user);
        }
        if (user.getRole() == Role.PATIENT) {
            Patient patient = Patient.builder()
                    .user(user)
                    .build();
            patientRepository.save(patient);
            user.setPatient(patient);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        userMapper.updateUser(user, request);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        uploadAvatar(user, request.getAvatar());

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
    }

    @Override
    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(userMapper::toUserResponse);
    }

    @Override
    public UserResponse getUserResponseByUsername(String username) {
        User user = getUserByUsername(username);
        UserResponse response = userMapper.toUserResponse(user);
        if (user.getRole() == Role.DOCTOR) {
            doctorRepository.findByUserId(user.getId())
                    .ifPresent(doctor -> response.setDoctorId(doctor.getId()));
        }
        return response;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @SuppressWarnings("unchecked")
    private void uploadAvatar(User user, MultipartFile avatar) {
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map<String, Object> uploadResult = cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar((String) uploadResult.get("url"));
            } catch (IOException e) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }
        }
    }
}