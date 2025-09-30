package com.trunghau1510.Clinic_Management.utils;

import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtils {
    public static Pageable createPageable(Integer page, Integer size, PaginationConfig config) {
        int pageNumber = (page != null && page >= 0) ? page : config.getDefaultPage();
        int pageSize = (size != null && size > 0) ? size : config.getDefaultSize();
        return PageRequest.of(pageNumber, pageSize);
    }
}