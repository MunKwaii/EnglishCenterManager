package vn.edu.ute.service.impl;



import vn.edu.ute.model.Teacher;
import vn.edu.ute.repository.TeacherRepository;
import vn.edu.ute.repository.impl.TeacherRepositoryImpl;
import vn.edu.ute.service.TeacherService;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository repo = new TeacherRepositoryImpl();

    // 1. Truy vấn danh sách giáo viên theo chuyên môn (Specialty)
    @Override
    public List<Teacher> findBySpecialty(String specialty) {
        return repo.findAll().stream()
                .filter(t -> t.getSpecialty().equalsIgnoreCase(specialty))
                .collect(Collectors.toList());
    }


    // 2. Kiểm tra xem Email đã tồn tại chưa (Dùng Lambda để check)
    @Override
    public boolean isEmailExists(String email) {
        return repo.findAll().stream()
                .anyMatch(t -> t.getEmail().equalsIgnoreCase(email));
    }

    // 3. Lấy danh sách tên của tất cả giáo viên (Dùng map)
    @Override
    public List<String> getAllTeacherNames() {
        return repo.findAll().stream()
                .map(Teacher::getFullName)
                .collect(Collectors.toList());
    }


    // 4. Tìm giáo viên theo số điện thoại (Sử dụng findFirst)
    @Override
    public Teacher findByPhone(String phone) {
        return repo.findAll().stream()
                .filter(t -> t.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }


    // 5. Đếm số lượng giáo viên đang hoạt động (Active)
    @Override
    public long countActiveTeachers() {
        return repo.findAll().stream()
                .filter(t -> "Active".equals(t.getStatus()))
                .count();
    }


    // 6. Sắp xếp danh sách giáo viên theo tên từ A-Z
    @Override
    public List<Teacher> getSortedTeachers() {
        return repo.findAll().stream()
                .sorted((t1, t2) -> t1.getFullName().compareToIgnoreCase(t2.getFullName()))
                .collect(Collectors.toList());
    }


    // --- CÁC HÀM CRUD  ---
    @Override
    public void addTeacher(Teacher t) throws Exception {
        // logic kiểm tra ở đây trước khi gọi repo
        if (t.getFullName() == null || t.getFullName().isEmpty()) {
            throw new Exception("Họ tên không được để trống!");
        }
        repo.save(t); // Repo này đã xử lý cả Thêm mới và Cập nhật
    }

    @Override
    public void deleteTeacher(Long id) throws Exception {
        repo.deleteById(id); //
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return repo.findAll(); //
    }
}