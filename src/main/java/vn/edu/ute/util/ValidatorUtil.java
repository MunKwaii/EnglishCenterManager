package vn.edu.ute.util;

import java.util.function.Predicate;

public class ValidatorUtil {
    
    // Sử dụng Lambda Predicate để kiểm tra chuỗi số tiền hợp lệ (chỉ chứa số và lớn hơn 0)
    public static final Predicate<String> isValidAmount = str -> {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            double val = Double.parseDouble(str);
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    // Kiểm tra ID không được rỗng
    public static final Predicate<String> isNotEmpty = str -> str != null && !str.trim().isEmpty();
}