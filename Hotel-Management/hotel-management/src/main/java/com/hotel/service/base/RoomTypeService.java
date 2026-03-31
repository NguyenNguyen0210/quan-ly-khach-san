package com.hotel.service.base;

import com.hotel.entity.RoomType;
import java.util.List;

public interface RoomTypeService {

    void addRoomType(RoomType roomType);

    RoomType getRoomTypeById(Long id);

    List<RoomType> getAllRoomTypes();

    RoomType getRoomTypeByName(String name);

    void updateRoomType(RoomType roomType);

    void deleteRoomType(Long roomTypeId);
}
