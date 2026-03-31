package com.hotel.controller;

import com.hotel.entity.ServiceUsage;
import com.hotel.service.base.ServiceUsageService;
import com.hotel.service.base.ServiceUsageServiceImpl;

import java.util.List;
import java.util.Objects;

public class ServiceUsageController {

    private final ServiceUsageService serviceUsageService = new ServiceUsageServiceImpl();

    public void addServiceUsage(Long bookingId, Long serviceId, int quantity) {
        serviceUsageService.addServiceUsage(bookingId, serviceId, quantity);
    }

    public List<ServiceUsage> getServiceUsageByBooking(Long bookingId) {
        if (bookingId == null) {
            return List.of();
        }
        return serviceUsageService.getServiceUsageByBooking(bookingId);
    }

    public Double calculateServiceUsageTotal(Long bookingId) {
        if (bookingId == null) {
            return 0.0;
        }
        return serviceUsageService.calculateServiceUsageTotal(bookingId);
    }

    public void removeServiceUsage(Long serviceUsageId) {
        serviceUsageService.removeServiceUsage(serviceUsageId);
    }
}
