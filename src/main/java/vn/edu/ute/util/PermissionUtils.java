package vn.edu.ute.util;

import javax.swing.*;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.model.enums.StaffRole;
import vn.edu.ute.model.enums.UserRole;

public class PermissionUtils {

    // Hàm applyMenuPermissions đã bị xóa do áp dụng Dynamic Sidebar

    /**
     * Kiểm tra user có quyền GỬI/SỬA/XÓA thông báo không
     * Chỉ Admin (UserRole) và Staff có role Admin/Manager mới được phép
     */
    public static boolean canManageNotifications(UserAccount user) {
        if (user == null) return false;

        // Admin (UserRole) luôn được phép
        if (user.getRole() == UserRole.Admin) {
            return true;
        }

        // Staff với StaffRole = Admin hoặc Manager được phép
        if (user.getRole() == UserRole.Staff && user.getStaff() != null) {
            StaffRole staffRole = user.getStaff().getRole();
            return staffRole == StaffRole.Admin || staffRole == StaffRole.Manager;
        }

        // Teacher, Student, Staff khác: không được phép
        return false;
    }

    /**
     * Kiểm tra user có quyền XEM tất cả thông báo không
     * Admin và Staff được xem hết, Teacher và Student chỉ xem của mình
     */
    public static boolean canViewAllNotifications(UserAccount user) {
        if (user == null) return false;
        return user.getRole() == UserRole.Admin || user.getRole() == UserRole.Staff;
    }

    /**
     * Kiểm tra user có quyền SỬA ĐIỂM không
     * Admin và Teacher được phép
     */
    public static boolean canEditScore(UserAccount user) {
        if (user == null) return false;
        return user.getRole() == UserRole.Admin || user.getRole() == UserRole.Teacher;
    }

    /**
     * Kiểm tra user có quyền XÓA HÓA ĐƠN không
     * Chỉ Admin (UserRole) và Staff có role Accountant/Admin/Manager được phép
     */
    public static boolean canDeleteInvoice(UserAccount user) {
        if (user == null) return false;
        if (user.getRole() == UserRole.Admin) return true;
        if (user.getRole() == UserRole.Staff && user.getStaff() != null) {
            StaffRole role = user.getStaff().getRole();
            return role == StaffRole.Admin || role == StaffRole.Manager || role == StaffRole.Accountant;
        }
        return false;
    }

    /**
     * Kiểm tra user có quyền QUẢN LÝ HỆ THỐNG (Account, Branch...) không
     * Chỉ duy nhất Admin được phép
     */
    public static boolean canManageSystem(UserAccount user) {
        if (user == null) return false;
        return user.getRole() == UserRole.Admin;
    }

    /**
     * Kiểm tra user có quyền điểm danh / nhập điểm lớp cụ thể không
     * Admin luôn true. Teacher phải là giáo viên của lớp đó.
     */
    public static boolean canTakeAttendance(UserAccount user, vn.edu.ute.model.AcademicClass academicClass) {
        if (user == null || academicClass == null) return false;
        if (user.getRole() == UserRole.Admin) return true;
        
        if (user.getRole() == UserRole.Teacher && user.getTeacher() != null) {
            if (academicClass.getTeacher() != null) {
                return user.getTeacher().getTeacherId().equals(academicClass.getTeacher().getTeacherId());
            }
        }
        return false;
    }
}