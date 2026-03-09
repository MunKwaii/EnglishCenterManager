package vn.edu.ute.service;

import vn.edu.ute.model.Room;
import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room addRoom(Room room);
    boolean removeRoom(Long id);

    // Sẽ dùng Lambda cho các hàm dưới 
    List<Room> getActiveRooms();
    List<Room> getRoomsByMinCapacity(int requiredCapacity);
    List<Room> searchRoomByName(String keyword);
}