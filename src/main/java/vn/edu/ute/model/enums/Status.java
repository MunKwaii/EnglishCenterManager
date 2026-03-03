package vn.edu.ute.model.enums;

/**
 * Chú thích: Enum quản lý trạng thái hoạt động chung cho toàn hệ thống.
 * Giúp đảm bảo tính nhất quán dữ liệu giữa Java và MySQL.
 */
public enum Status {
    Active,   // Chú thích: Đang hoạt động/Đang mở
    Inactive  // Chú thích: Ngừng hoạt động/Đã đóng
}