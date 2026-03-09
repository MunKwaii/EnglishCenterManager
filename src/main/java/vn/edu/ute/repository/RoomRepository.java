package vn.edu.ute.repository;

import vn.edu.ute.model.Room;
import java.util.List;

public interface RoomRepository {
    List<Room> findAll();
    Room findById(Long id);
    Room save(Room room);
    boolean delete(Long id);
}