package vn.edu.ute.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hàm băm mật khẩu (Dùng BCrypt tự sinh salt)
    public static String hash(String password) {
        // gensalt(10) là độ phức tạp mặc định, rất an toàn
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    // Kiểm tra mật khẩu nhập vào có khớp với BCrypt hash trong DB không
    public static boolean check(String input, String storedHash) {
        try {
            // BCrypt sẽ tự bóc tách salt từ chuỗi hash để đối chiếu
            return BCrypt.checkpw(input, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
}