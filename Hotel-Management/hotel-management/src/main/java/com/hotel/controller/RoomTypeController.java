package com.hotel.controller;

import com.hotel.entity.RoomType;
import com.hotel.service.base.RoomTypeService;
import com.hotel.service.base.RoomTypeServiceImpl;

import java.util.List;

public class RoomTypeController {

    private final RoomTypeService roomTypeService = new RoomTypeServiceImpl();

    public void addRoomType(RoomType roomType) {
        roomTypeService.addRoomType(roomType);
    }

    public RoomType getRoomTypeById(Long roomTypeId) {
        if (roomTypeId == null) {
            return null;
        }
        return roomTypeService.getRoomTypeById(roomTypeId);
    }

    public List<RoomType> getAllRoomTypes() {
        return roomTypeService.getAllRoomTypes();
    }

    public RoomType getRoomTypeByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return roomTypeService.getRoomTypeByName(name);
    }

    public void updateRoomType(RoomType roomType) {
        roomTypeService.updateRoomType(roomType);
    }

    public void deleteRoomType(Long roomTypeId) {
        roomTypeService.deleteRoomType(roomTypeId);
    }
}
