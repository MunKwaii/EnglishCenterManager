package vn.edu.ute.service.impl;

import vn.edu.ute.model.Room;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.repository.RoomRepository;
import vn.edu.ute.repository.impl.RoomRepositoryImpl;
import vn.edu.ute.service.RoomService;

import java.util.List;
import java.util.stream.Collectors;

public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepo = new RoomRepositoryImpl();

    @Override
    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    @Override
    public Room addRoom(Room room) {
        return roomRepo.save(room);
    }

    @Override
    public boolean removeRoom(Long id) {
        return roomRepo.delete(id);
    }

    @Override
    public List<Room> getActiveRooms() {
        List<Room> allRooms = roomRepo.findAll();
        if (allRooms == null) return List.of();

        // Lọc danh sách các phòng có trạng thái là Active
        return allRooms.stream()
                .filter(r -> r.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> getRoomsByMinCapacity(int requiredCapacity) {
        List<Room> allRooms = roomRepo.findAll();
        if (allRooms == null) return List.of();

        // Tìm các phòng học có sức chứa lớn hơn hoặc bằng sức chứa yêu cầu
        return allRooms.stream()
                .filter(r -> r.getCapacity() >= requiredCapacity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> searchRoomByName(String keyword) {
        List<Room> allRooms = roomRepo.findAll();
        if (allRooms == null) return List.of();

        //  kiếm phòng học theo tên, không phân biệt hoa/thường
        return allRooms.stream()
                .filter(r -> r.getRoomName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}