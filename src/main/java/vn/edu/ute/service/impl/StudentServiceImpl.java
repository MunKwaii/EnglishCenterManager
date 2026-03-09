package vn.edu.ute.service.impl;

import vn.edu.ute.model.Student;
import vn.edu.ute.model.enums.Gender;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.repository.StudentRepository;
import vn.edu.ute.repository.impl.StudentRepositoryImpl;
import vn.edu.ute.service.StudentService;

import java.util.List;
import java.util.stream.Collectors;

public class StudentServiceImpl implements StudentService {
    private final StudentRepository repo = new StudentRepositoryImpl();

    // 1. Lọc học viên theo giới tính (Gender Enum) - Thay cho Specialty của Teacher
    @Override
    public List<Student> filterByGender(Gender gender) {
        return repo.findAll().stream()
                .filter(s -> s.getGender() == gender)
                .collect(Collectors.toList());
    }

    // 2. Kiểm tra Email tồn tại (Lambda anyMatch)
    @Override
    public boolean isEmailExists(String email) {
        return repo.findAll().stream()
                .anyMatch(s -> s.getEmail().equalsIgnoreCase(email));
    }

    // 3. Lấy danh sách tên học viên (Lambda map)
    @Override
    public List<String> getAllStudentNames() {
        return repo.findAll().stream()
                .map(Student::getFullName)
                .collect(Collectors.toList());
    }

    // 4. Tìm học viên theo SĐT (Lambda findFirst)
    @Override
    public Student findByPhone(String phone) {
        return repo.findAll().stream()
                .filter(s -> s.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }

    // 5. Đếm số học viên đang hoạt động (Lambda filter & count)
    @Override
    public long countActiveStudents() {
        return repo.findAll().stream()
                .filter(s -> s.getStatus() == Status.Active)
                .count();
    }

    // 6. Sắp xếp học viên theo tên A-Z (Lambda sorted)
    @Override
    public List<Student> getSortedStudentsByName() {
        return repo.findAll().stream()
                .sorted((s1, s2) -> s1.getFullName().compareToIgnoreCase(s2.getFullName()))
                .collect(Collectors.toList());
    }

    // --- CÁC HÀM CRUD & VALIDATE ---
    @Override
    public void addStudent(Student s) throws Exception {
        // Kiểm tra trống các trường bắt buộc
        if (s.getFullName() == null || s.getFullName().isEmpty()) {
            throw new Exception("Họ tên học viên không được để trống!");
        }

        // Tận dụng Lambda để Validate trùng dữ liệu UNIQUE
        if (isEmailExists(s.getEmail())) {
            throw new Exception("Email này đã được đăng ký bởi học viên khác!");
        }

        if (findByPhone(s.getPhone()) != null) {
            throw new Exception("Số điện thoại này đã tồn tại!");
        }

        repo.save(s);
    }

    @Override
    public void updateStudent(Student s) throws Exception {
        if (s.getFullName() == null || s.getFullName().isEmpty()) {
            throw new Exception("Họ tên học viên không được để trống!");
        }

        // Kiểm tra trùng email nhưng bỏ qua chính sinh viên đang sửa (so sánh ID)
        boolean emailDuplicate = repo.findAll().stream()
                .anyMatch(existing ->
                        existing.getEmail().equalsIgnoreCase(s.getEmail())
                        && !existing.getStudentId().equals(s.getStudentId())
                );
        if (emailDuplicate) {
            throw new Exception("Email này đã được đăng ký bởi học viên khác!");
        }

        // Kiểm tra trùng SĐT nhưng bỏ qua chính sinh viên đang sửa
        boolean phoneDuplicate = repo.findAll().stream()
                .anyMatch(existing ->
                        existing.getPhone().equals(s.getPhone())
                        && !existing.getStudentId().equals(s.getStudentId())
                );
        if (phoneDuplicate) {
            throw new Exception("Số điện thoại này đã tồn tại!");
        }

        repo.save(s);
    }

    @Override
    public void deleteStudent(Long id) throws Exception {
        repo.deleteById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return repo.findAll();
    }
}