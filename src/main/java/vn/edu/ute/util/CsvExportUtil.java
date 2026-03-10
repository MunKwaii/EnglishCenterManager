package vn.edu.ute.util;

import vn.edu.ute.model.Payment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExportUtil {

    // Hàm xuất danh sách thanh toán ra file CSV dùng Stream API
    public static void exportPaymentsToCsv(List<Payment> payments, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        // Tạo header
        String header = "ID Thanh Toan,ID Hoa Don,Sinh Vien,So Tien,Phuong Thuc,Trang Thai,Ma Giao Dich\n";
        
        // Map List<Payment> thành List<String> dạng CSV
        String csvData = payments.stream()
                .map(p -> String.format("%s,%s,%s,%s,%s,%s,%s",
                        p.getPaymentId() != null ? p.getPaymentId() : "N/A",
                        p.getInvoice() != null ? p.getInvoice().getInvoiceId() : "N/A",
                        p.getStudent() != null ? p.getStudent().getFullName() : "N/A",
                        p.getAmount(),
                        p.getPaymentMethod(),
                        p.getStatus(),
                        p.getReferenceCode() != null ? p.getReferenceCode() : ""))
                .collect(Collectors.joining("\n"));

        Files.write(path, (header + csvData).getBytes());
    }
}