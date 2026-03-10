package vn.edu.ute.service.impl;

import vn.edu.ute.model.Staff;
import vn.edu.ute.repository.StaffRepository;
import vn.edu.ute.repository.impl.StaffRepositoryImpl;
import vn.edu.ute.service.StaffService;
import java.util.List;

public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepo = new StaffRepositoryImpl();

    @Override
    public void saveStaff(Staff staff) throws Exception {
        // Có thể thêm logic validate email/sđt ở đây trước khi lưu
        staffRepo.save(staff);
    }

    @Override
    public List<Staff> getActiveStaffs() throws Exception {
        return staffRepo.findAllActive();
    }
}