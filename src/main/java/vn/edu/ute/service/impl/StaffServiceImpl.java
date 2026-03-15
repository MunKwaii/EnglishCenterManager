package vn.edu.ute.service.impl;

import vn.edu.ute.model.Staff;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.repository.StaffRepository;
import vn.edu.ute.repository.impl.StaffRepositoryImpl;
import vn.edu.ute.service.StaffService;

import java.util.List;

public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepo = new StaffRepositoryImpl();

    @Override
    public boolean isEmailExists(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) return false;
        return staffRepo.findByEmail(email.trim()).isPresent();
    }

    @Override
    public Staff findByPhone(String phone) throws Exception {
        if (phone == null || phone.trim().isEmpty()) return null;
        return staffRepo.findByPhone(phone.trim()).orElse(null);
    }

    @Override
    public List<Staff> getActiveStaffs() throws Exception {
        return staffRepo.findAllActive();
    }

    @Override
    public List<Staff> getAllStaffs() throws Exception {
        return staffRepo.findAll();
    }

    @Override
    public void addStaff(Staff staff) throws Exception {
        validateRequired(staff);

        // Unique checks
        if (isEmailExists(staff.getEmail())) {
            throw new Exception("Email này đã được sử dụng bởi nhân sự khác!");
        }
        if (findByPhone(staff.getPhone()) != null) {
            throw new Exception("Số điện thoại này đã tồn tại!");
        }

        if (staff.getStatus() == null) staff.setStatus(Status.Active);
        staffRepo.save(staff);
    }

    @Override
    public void updateStaff(Staff staff) throws Exception {
        if (staff == null || staff.getStaffId() == null) {
            throw new Exception("Vui lòng chọn nhân sự cần cập nhật!");
        }
        validateRequired(staff);

        // Duplicate email but ignore current staffId
        boolean emailDuplicate = staffRepo.findAll().stream().anyMatch(existing ->
                existing.getEmail() != null
                        && existing.getEmail().equalsIgnoreCase(staff.getEmail())
                        && existing.getStaffId() != null
                        && !existing.getStaffId().equals(staff.getStaffId())
        );
        if (emailDuplicate) {
            throw new Exception("Email này đã được sử dụng bởi nhân sự khác!");
        }

        // Duplicate phone but ignore current staffId
        boolean phoneDuplicate = staffRepo.findAll().stream().anyMatch(existing ->
                existing.getPhone() != null
                        && existing.getPhone().equals(staff.getPhone())
                        && existing.getStaffId() != null
                        && !existing.getStaffId().equals(staff.getStaffId())
        );
        if (phoneDuplicate) {
            throw new Exception("Số điện thoại này đã tồn tại!");
        }

        staffRepo.save(staff);
    }

    @Override
    public void deleteStaff(Long id) throws Exception {
        staffRepo.deleteById(id);
    }

    private static void validateRequired(Staff staff) throws Exception {
        if (staff == null) {
            throw new Exception("Dữ liệu nhân sự không hợp lệ!");
        }
        if (staff.getFullName() == null || staff.getFullName().trim().isEmpty()) {
            throw new Exception("Họ tên nhân sự không được để trống!");
        }
        if (staff.getEmail() == null || staff.getEmail().trim().isEmpty()) {
            throw new Exception("Email không được để trống!");
        }
        if (staff.getPhone() == null || staff.getPhone().trim().isEmpty()) {
            throw new Exception("Số điện thoại không được để trống!");
        }
        if (staff.getRole() == null) {
            throw new Exception("Vui lòng chọn vai trò!");
        }
    }
}