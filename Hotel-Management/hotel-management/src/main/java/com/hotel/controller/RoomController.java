package com.hotel.controller;

import com.hotel.entity.Room;
import com.hotel.service.base.RoomService;
import com.hotel.service.base.RoomServiceImpl;

import java.util.List;
import java.util.Objects;

public class RoomController {

    private final RoomService roomService = new RoomServiceImpl();

    public void addRoom(Room room) {
        roomService.addRoom(room);
    }

    public Room getRoomById(Long roomId) {
        if (roomId == null) {
            return null;
        }
        return roomService.getRoomById(roomId);
    }

    public Room getRoomByNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.isBlank()) {
            return null;
        }
        return roomService.getRoomByNumber(roomNumber);
    }

    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    public List<Room> getRoomsByStatus(String status) {
        if (status == null || status.isBlank()) {
            return List.of();
        }
        return roomService.getRoomsByStatus(status);
    }

    public List<Room> getRoomsByTypeId(Long roomTypeId) {
        if (roomTypeId == null) {
            return List.of();
        }
        return roomService.getRoomsByTypeId(roomTypeId);
    }

    public void updateRoom(Room room) {
        roomService.updateRoom(room);
    }

    public void changeRoomStatus(Long roomId, String status) {
        Objects.requireNonNull(status, "Room status is required");
        roomService.changeRoomStatus(roomId, status);
    }

    public void deleteRoom(Long roomId) {
        roomService.deleteRoom(roomId);
    }
}
