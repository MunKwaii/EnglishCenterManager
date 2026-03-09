package vn.edu.ute.util;

import vn.edu.ute.model.UserAccount;

public class UserSession {
    private static UserAccount currentUser;

    public static void login(UserAccount user) {
        currentUser = user;
    }
    public static void logout() {
        currentUser = null;
    }
    public static UserAccount getCurrentUser() {
        return currentUser;
    }
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}