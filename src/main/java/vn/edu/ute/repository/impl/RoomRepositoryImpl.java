package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Room;
import vn.edu.ute.repository.RoomRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class RoomRepositoryImpl implements RoomRepository {
    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<Room> findAll() {
        try {
            return txManager.runInTransaction(em ->
                    em.createQuery("SELECT r FROM Room r LEFT JOIN FETCH r.branch", Room.class).getResultList()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Room findById(Long id) {
        try {
            return txManager.runInTransaction(em -> {
                List<Room> rooms = em.createQuery("SELECT r FROM Room r LEFT JOIN FETCH r.branch WHERE r.roomId = :id", Room.class)
                        .setParameter("id", id)
                        .getResultList();
                return rooms.isEmpty() ? null : rooms.get(0);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Room save(Room room) {
        try {
            return txManager.runInTransaction(em -> {
                if (room.getRoomId() == null) {
                    em.persist(room); // Thêm phòng mới
                    return room;
                } else {
                    return em.merge(room); // Cập nhật phòng đã có
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            return txManager.runInTransaction(em -> {
                Room room = em.find(Room.class, id);
                if (room != null) {
                    em.remove(room);
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}