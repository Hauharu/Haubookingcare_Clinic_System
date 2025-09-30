package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.service.StatsService;
import com.trunghau1510.Clinic_Management.repository.StatsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsServiceImplement implements StatsService {
    StatsRepository statsRepository;

    @Override
    public List<Object[]> statsCountExaminedTotalAmount(Integer year, Integer quarter, Integer month) {
        return statsRepository.statsCountExaminedTotalAmount(year, quarter, month);
    }

    @Override
    public List<Object[]> statsDiagnosedCountExamined(Integer year, Integer quarter, Integer month) {
        return statsRepository.statsDiagnosedCountExamined(year, quarter, month);
    }
}
