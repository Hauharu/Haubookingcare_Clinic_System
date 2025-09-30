package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Notification;
import com.trunghau1510.Clinic_Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUser_IdOrderByCreatedAtDesc(String userId);
    long countByUserAndIsReadFalse(User user);

}
