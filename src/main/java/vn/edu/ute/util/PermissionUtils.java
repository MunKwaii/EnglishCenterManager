package vn.edu.ute.util;

import javax.swing.*;
import vn.edu.ute.model.enums.UserRole;

public class PermissionUtils {

    /**
     * Hàm kiểm tra và ẩn/hiện các nút trên Sidebar dựa vào Role
     * @param role Quyền hiện tại
     * @param btnTeacher Nút menu Giáo viên
     * @param btnStudent Nút menu Sinh viên
     * @param btnClass Nút menu Lớp học
     * @param btnAccount Nút menu Tài khoản
     */
    public static void applyMenuPermissions(UserRole role, JButton btnTeacher, JButton btnStudent, JButton btnClass, JButton btnAccount) {
        // Mặc định Admin thấy hết, nên ta chỉ xử lý các Role thấp hơn
        if (role == UserRole.Admin) return;

        // 1. Nếu là Student: Ẩn gần như hết, chỉ để lại Lớp học và Thông báo (nếu có)
        if (role == UserRole.Student) {
            btnTeacher.setVisible(false);
            btnStudent.setVisible(false);
            if (btnAccount != null) btnAccount.setVisible(false);
        }

        // 2. Nếu là Teacher: Không cho quản lý giáo viên khác và tài khoản
        if (role == UserRole.Teacher) {
            btnTeacher.setVisible(false);
            if (btnAccount != null) btnAccount.setVisible(false);
        }

        // 3. Nếu là Staff (Nhân viên): Có thể cho xem mọi thứ trừ Tài khoản hệ thống
        if (role == UserRole.Staff) {
            if (btnAccount != null) btnAccount.setVisible(false);
        }
    }
}