package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.AppointmentCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.request.AppointmentStatusUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.AppointmentResponse;
import com.trunghau1510.Clinic_Management.entity.Appointment;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse registerAppointment(AppointmentCreationRequest request);
    List<AppointmentResponse> getAppointments(String patientId, String doctorId, Integer page, Integer size);
    AppointmentResponse updateAppointment(String id, AppointmentUpdateRequest request);
    AppointmentResponse updateStatus(String id, AppointmentStatusUpdateRequest request);
    void deleteAppointment(String id);
    AppointmentResponse getAppointmentById(String id);
    Appointment getAppointmentEntityById(String id);
}
