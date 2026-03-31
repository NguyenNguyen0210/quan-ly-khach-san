package com.hotel.service.base;

import com.hotel.entity.Room;
import java.util.List;

public interface RoomService {

    void addRoom(Room room);

    Room getRoomById(Long roomId);

    Room getRoomByNumber(String roomNumber);

    List<Room> getAllRooms();

    List<Room> getRoomsByStatus(String status);

    List<Room> getRoomsByTypeId(Long roomTypeId);

    void updateRoom(Room room);

    void changeRoomStatus(Long roomId, String status);

    void deleteRoom(Long roomId);
}
