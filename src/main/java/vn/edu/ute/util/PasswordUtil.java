package vn.edu.ute.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordUtil {
    // Hàm băm mật khẩu
    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa!");
        }
    }

    // Kiểm tra mật khẩu nhập vào có khớp với hash trong DB không
    public static boolean check(String input, String storedHash) {
        return hash(input).equals(storedHash);
    }
}