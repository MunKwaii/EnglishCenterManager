package vn.edu.ute.util;

import vn.edu.ute.model.Payment;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExportUtil {

    // Hàm xuất danh sách thanh toán ra file CSV dùng Stream API
    public static void exportPaymentsToCsv(List<Payment> payments, String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // Tạo header (dùng tên thuần ASCII để tránh mọi rủi ro)
        String header = "ID Thanh Toan,ID Hoa Don,Ho Ten Sinh Vien,So Tien,Phuong Thuc Thanh Toan,Trang Thai,Ma Giao Dich\n";

        // Map List<Payment> thành List<String> dạng CSV
        String csvData = payments.stream()
                .map(p -> String.format("%s,%s,%s,%s,%s,%s,%s",
                        p.getPaymentId() != null ? p.getPaymentId() : "N/A",
                        p.getInvoice() != null ? p.getInvoice().getInvoiceId() : "N/A",
                        escapeCsv(p.getStudent() != null ? p.getStudent().getFullName() : "N/A"),
                        p.getAmount(),
                        p.getPaymentMethod(),
                        p.getStatus(),
                        escapeCsv(p.getReferenceCode() != null ? p.getReferenceCode() : "")))
                .collect(Collectors.joining("\n"));

        // Ghi file với BOM UTF-8 (EF BB BF) để Excel nhận diện đúng tiếng Việt
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8))) {
            // Ghi BOM
            writer.write('\uFEFF');
            writer.write(header);
            writer.write(csvData);
        }
    }

    /**
     * Escape giá trị CSV: nếu chứa dấu phẩy, ngoặc kép, hoặc xuống dòng thì bọc
     * trong ""
     */
    private static String escapeCsv(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
