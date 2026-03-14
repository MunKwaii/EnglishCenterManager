package vn.edu.ute.util;

import vn.edu.ute.model.enums.UserRole;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuConstants {

    // Danh sách các Module trong ứng dụng
    public static final String MODULE_TEACHER = "Quản lý Giáo viên";
    public static final String MODULE_STUDENT = "Quản lý Học viên";
    public static final String MODULE_CLASS = "Quản lý Lớp học";
    public static final String MODULE_COURSE = "Quản lý Khóa học";
    public static final String MODULE_FINANCE = "Quản lý Tài chính";
    public static final String MODULE_SCHEDULE = "Quản lý Lịch biểu";
    public static final String MODULE_NOTIFICATION = "Thông báo hệ thống";
    public static final String MODULE_ACCOUNT = "Quản lý Tài khoản (Hệ thống)";
    public static final String MODULE_BRANCH = "Quản lý Chi nhánh";
    public static final String MODULE_ROOM = "Quản lý Phòng học";
    public static final String MODULE_PROMOTION = "Quản lý Khuyến mãi";
    public static final String MODULE_PLACEMENT_TEST = "Bài Test Đầu vào";
    public static final String MODULE_ENROLLMENT = "Ghi danh Học viên";

    private static final Map<String, List<UserRole>> modulePermissions = new HashMap<>();

    static {
        // Admin được truy cập vào tất cả các module
        List<UserRole> allRoles = Arrays.asList(UserRole.Admin, UserRole.Teacher, UserRole.Student, UserRole.Staff);
        List<UserRole> staffAndAdmin = Arrays.asList(UserRole.Admin, UserRole.Staff);

        modulePermissions.put(MODULE_TEACHER, staffAndAdmin); // Thường Teacher không quản lý chức năng danh sách
                                                              // Teacher hệ thống
        modulePermissions.put(MODULE_STUDENT, staffAndAdmin);
        modulePermissions.put(MODULE_CLASS, allRoles); // Teacher và Student cũng có thể xem Lớp Học của bản thân
        modulePermissions.put(MODULE_COURSE, staffAndAdmin);
        modulePermissions.put(MODULE_FINANCE, staffAndAdmin); // Sinh viên chỉ xem qua module cá nhân
        modulePermissions.put(MODULE_SCHEDULE, allRoles);
        modulePermissions.put(MODULE_NOTIFICATION, allRoles);

        // Modules đặc quyền
        modulePermissions.put(MODULE_ACCOUNT, Arrays.asList(UserRole.Admin));
        modulePermissions.put(MODULE_BRANCH, Arrays.asList(UserRole.Admin));
        modulePermissions.put(MODULE_ROOM, staffAndAdmin);
        modulePermissions.put(MODULE_PROMOTION, staffAndAdmin);
        modulePermissions.put(MODULE_PLACEMENT_TEST, staffAndAdmin);
        modulePermissions.put(MODULE_ENROLLMENT, staffAndAdmin);
    }

    /**
     * Kiểm tra xem Role hiện tại có quyền truy cập vào Module cụ thể hay không
     */
    public static boolean isModuleAllowed(String moduleName, UserRole role) {
        if (role == UserRole.Admin)
            return true;
        List<UserRole> allowedRoles = modulePermissions.get(moduleName);
        if (allowedRoles != null) {
            return allowedRoles.contains(role);
        }
        return false;
    }
}
