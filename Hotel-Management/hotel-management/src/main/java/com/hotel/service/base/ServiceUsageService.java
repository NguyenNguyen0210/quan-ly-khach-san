package com.hotel.service.base;

import com.hotel.entity.ServiceUsage;
import java.util.List;

public interface ServiceUsageService {

    void addServiceUsage(Long bookingId, Long serviceId, int quantity);

    List<ServiceUsage> getServiceUsageByBooking(Long bookingId);

    Double calculateServiceUsageTotal(Long bookingId);

    void removeServiceUsage(Long serviceUsageId);
}
