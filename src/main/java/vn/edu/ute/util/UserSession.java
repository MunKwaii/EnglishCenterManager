package vn.edu.ute.util;

import vn.edu.ute.model.UserAccount;

public class UserSession {
    private static UserAccount currentUser;
    private static Long teacherId;
    private static Long studentId;
    private static Long staffId;

    public static void login(UserAccount user) {
        currentUser = user;
        if (user != null) {
            if (user.getTeacher() != null) teacherId = user.getTeacher().getTeacherId();
            if (user.getStudent() != null) studentId = user.getStudent().getStudentId();
            if (user.getStaff() != null) staffId = user.getStaff().getStaffId();
        }
    }
    
    public static void logout() {
        currentUser = null;
        teacherId = null;
        studentId = null;
        staffId = null;
    }
    
    public static UserAccount getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static Long getTeacherId() { return teacherId; }
    public static Long getStudentId() { return studentId; }
    public static Long getStaffId() { return staffId; }
}