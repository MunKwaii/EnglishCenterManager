package vn.edu.ute.view;

import vn.edu.ute.model.Certificate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterJob;

public class StudentCertificateDialog extends JDialog {
    private Certificate certificate;

    public StudentCertificateDialog(Window owner, Certificate certificate) {
        super((Frame) owner, "Chứng nhận hoàn thành khóa học", true);
        this.certificate = certificate;

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel pnlCert = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Nền
                g2d.setColor(new Color(250, 250, 250));
                g2d.fillRect(0, 0, w, h);

                // Viền ngoài
                g2d.setColor(new Color(212, 175, 55)); // Màu vàng gold
                g2d.setStroke(new BasicStroke(10));
                g2d.drawRect(20, 20, w - 40, h - 40);

                // Viền trong
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(30, 30, w - 60, h - 60);

                // Header
                g2d.setColor(new Color(44, 62, 80));
                g2d.setFont(new Font("Serif", Font.BOLD, 40));
                FontMetrics fm = g2d.getFontMetrics();
                String title = "CERTIFICATE OF COMPLETION";
                g2d.drawString(title, (w - fm.stringWidth(title)) / 2, 120);

                g2d.setFont(new Font("Serif", Font.ITALIC, 20));
                String subTitle = "This is to certify that";
                fm = g2d.getFontMetrics();
                g2d.drawString(subTitle, (w - fm.stringWidth(subTitle)) / 2, 170);

                // Tên Học viên
                g2d.setColor(new Color(192, 57, 43)); // Đỏ sậm
                g2d.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 45));
                String studentName = certificate.getStudent().getFullName().toUpperCase();
                fm = g2d.getFontMetrics();
                g2d.drawString(studentName, (w - fm.stringWidth(studentName)) / 2, 240);

                // Đường kẻ dưới tên
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(w/4, 250, 3*w/4, 250);

                // Nội dung
                g2d.setFont(new Font("Serif", Font.PLAIN, 22));
                String content1 = "has successfully completed the course";
                fm = g2d.getFontMetrics();
                g2d.drawString(content1, (w - fm.stringWidth(content1)) / 2, 310);

                g2d.setFont(new Font("Serif", Font.BOLD, 28));
                String courseName = certificate.getAcademicClass().getCourse() != null ? 
                    certificate.getAcademicClass().getCourse().getCourseName() : 
                    certificate.getAcademicClass().getClassName();
                fm = g2d.getFontMetrics();
                g2d.drawString(courseName, (w - fm.stringWidth(courseName)) / 2, 360);

                // Thông tin thêm
                g2d.setFont(new Font("Serif", Font.PLAIN, 16));
                g2d.drawString("Serial No: " + certificate.getSerialNo(), 80, 480);
                
                String dateStr = certificate.getIssueDate() != null ? certificate.getIssueDate().toString() : "N/A";
                g2d.drawString("Date: " + dateStr, 80, 510);

                // Chữ ký (Mô phỏng)
                g2d.drawLine(w - 250, 480, w - 80, 480);
                g2d.drawString("Director Signature", w - 230, 510);
            }
        };

        pnlCert.setPreferredSize(new Dimension(800, 550));

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnPrint = new JButton("In Chứng Nhận");
        btnPrint.setFont(new Font("Arial", Font.BOLD, 14));
        btnPrint.setBackground(new Color(52, 152, 219));
        btnPrint.setForeground(Color.WHITE);
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));

        btnPrint.addActionListener(e -> {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((g, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;
                
                // Thu nhỏ tỷ lệ để in cho vừa trang A4 (cơ bản)
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                double scaleX = pageFormat.getImageableWidth() / pnlCert.getWidth();
                double scaleY = pageFormat.getImageableHeight() / pnlCert.getHeight();
                double scale = Math.min(scaleX, scaleY);
                g2d.scale(scale, scale);
                
                pnlCert.printAll(g);
                return java.awt.print.Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi in: " + ex.getMessage());
                }
            }
        });

        btnClose.addActionListener(e -> dispose());

        pnlBottom.add(btnPrint);
        pnlBottom.add(btnClose);

        add(pnlCert, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }
}
