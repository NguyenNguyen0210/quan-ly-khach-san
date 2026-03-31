package com.hotel.service.base;

import com.hotel.dao.RoomTypeDAO;
import com.hotel.entity.RoomType;

import java.util.List;
import java.util.Objects;

public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    @Override
    public void addRoomType(RoomType roomType) {
        Objects.requireNonNull(roomType, "Room type must not be null");
        Objects.requireNonNull(roomType.getName(), "Room type name is required");
        roomTypeDAO.save(roomType);
    }

    @Override
    public RoomType getRoomTypeById(Long id) {
        if (id == null) {
            return null;
        }
        return roomTypeDAO.findById(id);
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return roomTypeDAO.findAll();
    }

    @Override
    public RoomType getRoomTypeByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return roomTypeDAO.findByName(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateRoomType(RoomType roomType) {
        Objects.requireNonNull(roomType, "Room type must not be null");
        if (roomType.getId() == null) {
            throw new IllegalArgumentException("Room type id is required for update.");
        }
        roomTypeDAO.update(roomType);
    }

    @Override
    public void deleteRoomType(Long roomTypeId) {
        if (roomTypeId == null) {
            throw new IllegalArgumentException("Room type id is required for delete.");
        }
        RoomType existing = roomTypeDAO.findById(roomTypeId);
        if (existing == null) {
            return;
        }
        roomTypeDAO.delete(existing);
    }
}
