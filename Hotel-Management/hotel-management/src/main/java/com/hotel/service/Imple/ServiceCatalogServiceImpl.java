package com.hotel.service.base;

import com.hotel.dao.ServiceDAO;
import com.hotel.entity.Service;

import java.util.List;
import java.util.Objects;

public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    public void addService(Service service) {
        Objects.requireNonNull(service, "Service must not be null");
        Objects.requireNonNull(service.getName(), "Service name is required");
        if (serviceDAO.findByName(service.getName()) != null) {
            throw new IllegalStateException("Service name already exists.");
        }
        if (service.getPrice() == null || service.getPrice() < 0) {
            throw new IllegalArgumentException("Service price must be non-negative.");
        }
        serviceDAO.save(service);
    }

    @Override
    public Service getServiceById(Long id) {
        if (id == null) {
            return null;
        }
        return serviceDAO.findById(id);
    }

    @Override
    public Service getServiceByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return serviceDAO.findByName(name);
    }

    @Override
    public List<Service> getAllServices() {
        return serviceDAO.findAll();
    }

    @Override
    public void updateService(Service service) {
        Objects.requireNonNull(service, "Service must not be null");
        if (service.getId() == null) {
            throw new IllegalArgumentException("Service id is required for update.");
        }
        serviceDAO.update(service);
    }

    @Override
    public void deleteService(Long serviceId) {
        if (serviceId == null) {
            throw new IllegalArgumentException("Service id is required for delete.");
        }
        Service existing = serviceDAO.findById(serviceId);
        if (existing == null) {
            return;
        }
        serviceDAO.delete(existing);
    }

    @Override
    public void adjustServiceQuantity(Long serviceId, int delta) {
        if (serviceId == null) {
            throw new IllegalArgumentException("Service id is required.");
        }
        Service service = serviceDAO.findById(serviceId);
        if (service == null) {
            throw new IllegalArgumentException("Service not found with id " + serviceId);
        }
        Integer currentQuantity = service.getQuantity();
        if (currentQuantity == null) {
            currentQuantity = 0;
        }
        int updated = currentQuantity + delta;
        if (updated < 0) {
            throw new IllegalStateException("Service quantity cannot go below zero.");
        }
        service.setQuantity(updated);
        serviceDAO.update(service);
    }
}
