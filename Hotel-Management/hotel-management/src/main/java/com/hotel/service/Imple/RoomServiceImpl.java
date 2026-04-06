package com.hotel.service.base;

import com.hotel.dao.RoomDAO;
import com.hotel.entity.Room;

import java.util.List;
import java.util.Objects;

public class RoomServiceImpl implements RoomService {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    public void addRoom(Room room) {
        Objects.requireNonNull(room, "Room must not be null");
        Objects.requireNonNull(room.getRoomNumber(), "Room number is required");
        if (roomDAO.findByRoomNumber(room.getRoomNumber()) != null) {
            throw new IllegalStateException("Room number already exists.");
        }
        roomDAO.save(room);
    }

    @Override
    public Room getRoomById(Long roomId) {
        if (roomId == null) {
            return null;
        }
        Room room = roomDAO.findByIdWithRoomType(roomId);
        return room != null ? room : roomDAO.findById(roomId);
    }

    @Override
    public Room getRoomByNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.isBlank()) {
            return null;
        }
        return roomDAO.findByRoomNumber(roomNumber);
    }

    @Override
    public List<Room> getAllRooms() {
        try {
            return roomDAO.findAllWithRoomType();
        } catch (Exception ex) {
            return roomDAO.findAll();
        }
    }

    @Override
    public List<Room> getRoomsByStatus(String status) {
        if (status == null || status.isBlank()) {
            return List.of();
        }
        return roomDAO.findByStatus(status);
    }

    @Override
    public List<Room> getRoomsByTypeId(Long roomTypeId) {
        if (roomTypeId == null) {
            return List.of();
        }
        return roomDAO.findByRoomTypeId(roomTypeId);
    }

    @Override
    public void updateRoom(Room room) {
        Objects.requireNonNull(room, "Room must not be null");
        if (room.getId() == null) {
            throw new IllegalArgumentException("Room id is required for update.");
        }
        roomDAO.update(room);
    }

    @Override
    public void changeRoomStatus(Long roomId, String status) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room id is required.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Room status is required.");
        }
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found with id " + roomId);
        }
        room.setStatus(status);
        roomDAO.update(room);
    }

    @Override
    public void deleteRoom(Long roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room id is required for delete.");
        }
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            return;
        }
        roomDAO.delete(room);
    }
}
