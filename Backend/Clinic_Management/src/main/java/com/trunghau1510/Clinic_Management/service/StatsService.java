package com.trunghau1510.Clinic_Management.service;

import java.util.List;

public interface StatsService {

    List<Object[]> statsCountExaminedTotalAmount(Integer year, Integer quarter, Integer month);
    List<Object[]> statsDiagnosedCountExamined(Integer year, Integer quarter, Integer month);
}
