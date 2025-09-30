package com.trunghau1510.Clinic_Management.service;
import com.trunghau1510.Clinic_Management.entity.User;

public interface EmailService {

    void sendDoctorAccountCreatedEmail(User user, String tempUsername, String tempPassword);
    void sendDoctorRejectionEmail(User user);
    void sendPaymentSuccessEmail(String toEmail, String patientName, String amount, String transactionId);
    void sendAppointmentConfirmation(String toEmail, String subject, String patientName, String doctorName, String time);
    void sendAppointmentConfirmationtoDoctor(String toEmailDoctor, String subject, String patientName, String doctorName, String time);
    void sendAppointmentCancellation(String toEmail, String subject, String patientName, String doctorName, String time);
    void sendAppointmentCancellationToDoctor(String toEmailDoctor, String subject, String patientName, String doctorName, String time);
    void sendDoctorApprovalEmail(User user);
}
