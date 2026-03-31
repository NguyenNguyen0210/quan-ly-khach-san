package com.hotel.service.base;

import com.hotel.entity.Service;
import java.util.List;

public interface ServiceCatalogService {

    void addService(Service service);

    Service getServiceById(Long id);

    Service getServiceByName(String name);

    List<Service> getAllServices();

    void updateService(Service service);

    void deleteService(Long serviceId);

    void adjustServiceQuantity(Long serviceId, int delta);
}
