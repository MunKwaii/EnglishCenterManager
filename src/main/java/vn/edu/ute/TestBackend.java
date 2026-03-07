package vn.edu.ute;

import vn.edu.ute.model.Staff;
import vn.edu.ute.model.enums.StaffRole;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.StaffService;
import vn.edu.ute.service.impl.StaffServiceImpl;

import java.util.List;

public class TestBackend {
    public static void main(String[] args) {
        System.out.println("--- BẮT ĐẦU TEST BACKEND NHÂN SỰ ---");

        // Khởi tạo Service
        StaffService staffService = new StaffServiceImpl();

        try {
            // 1. Test chức năng Thêm mới Nhân sự (Create)
            System.out.println("Đang lưu nhân sự mới vào Database...");
            Staff newStaff = Staff.builder()
                    .fullName("Lê Ngô Nhựt Tân")
                    .role(StaffRole.Manager)
                    .phone("0123456789")
                    .email("tan.lengonhut@example.com")
                    .status(Status.Active)
                    .build();
            
            staffService.saveStaff(newStaff);
            System.out.println("=> Lưu thành công! ID được cấp: " + newStaff.getStaffId());

            // 2. Test chức năng Lấy danh sách (Read)
            System.out.println("\nĐang tải danh sách nhân sự đang hoạt động...");
            List<Staff> activeStaffs = staffService.getActiveStaffs();
            
            // Dùng Lambda forEach để in danh sách
            activeStaffs.forEach(staff -> {
                System.out.println("- Nhân sự: " + staff.getFullName() 
                                 + " | Chức vụ: " + staff.getRole() 
                                 + " | Email: " + staff.getEmail());
            });

        } catch (Exception e) {
            System.err.println(">>> CÓ LỖI XẢY RA TRONG QUÁ TRÌNH TEST!");
            e.printStackTrace();
        } finally {
            // Đóng kết nối JPA để kết thúc chương trình hoàn toàn
            vn.edu.ute.util.Jpa.shutdown();
            System.out.println("--- KẾT THÚC TEST ---");
        }
    }
}