package vn.edu.ute;

import vn.edu.ute.util.TransactionManager;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo TransactionManager để quản lý phiên làm việc với DB
        TransactionManager txManager = new TransactionManager();

        System.out.println("--- Starting Database Connection Test ---");

        try {
            // Chú thích: Sử dụng Lambda để thực hiện một câu truy vấn kiểm tra
            String result = txManager.runInTransaction(em -> {
                // Chú thích: Chạy câu lệnh SQL đơn giản nhất để xác nhận kết nối tới MySQL
                Object status = em.createNativeQuery("SELECT 1").getSingleResult();
                return status != null ? "Success" : "Failed";
            });

            if ("Success".equals(result)) {
                System.out.println(">>> Connection to MySQL 'mis_language_center' is SUCCESSFUL!");
            }
        } catch (Exception e) {
            // Chú thích: Nếu có lỗi (sai pass, sai tên DB...), chương trình sẽ in ra tại đây
            System.err.println(">>> Connection FAILED!");
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Chú thích: Luôn đóng kết nối khi kết thúc ứng dụng
            vn.edu.ute.util.Jpa.shutdown();
        }
    }
}