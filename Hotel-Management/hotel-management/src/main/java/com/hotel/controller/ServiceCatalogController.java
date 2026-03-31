package com.hotel.controller;

import com.hotel.entity.Service;
import com.hotel.service.base.ServiceCatalogService;
import com.hotel.service.base.ServiceCatalogServiceImpl;

import java.util.List;
import java.util.Objects;

public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService = new ServiceCatalogServiceImpl();

    public void addService(Service service) {
        serviceCatalogService.addService(service);
    }

    public Service getServiceById(Long serviceId) {
        if (serviceId == null) {
            return null;
        }
        return serviceCatalogService.getServiceById(serviceId);
    }

    public Service getServiceByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return serviceCatalogService.getServiceByName(name);
    }

    public List<Service> getAllServices() {
        return serviceCatalogService.getAllServices();
    }

    public void updateService(Service service) {
        serviceCatalogService.updateService(service);
    }

    public void deleteService(Long serviceId) {
        serviceCatalogService.deleteService(serviceId);
    }

    public void adjustServiceQuantity(Long serviceId, int delta) {
        serviceCatalogService.adjustServiceQuantity(serviceId, delta);
    }
}
