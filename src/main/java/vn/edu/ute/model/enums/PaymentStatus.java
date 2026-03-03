package vn.edu.ute.model.enums;

public enum PaymentStatus {
    Pending,    // Đang chờ xử lý
    Completed,  // Đã thanh toán thành công
    Failed,     // Giao dịch lỗi
    Refunded    // Đã hoàn tiền
}