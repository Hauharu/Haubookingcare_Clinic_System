package com.trunghau1510.Clinic_Management.repository;

import com.trunghau1510.Clinic_Management.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByInvoiceId(String invoiceId);

    Optional<Payment> findByTransactionId(String transactionId);

}
