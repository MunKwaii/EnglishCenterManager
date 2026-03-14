package vn.edu.ute.view;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.impl.FinanceServiceImpl;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class StudentFeeDialog extends JDialog {
    private FinanceService financeService = new FinanceServiceImpl();
    private JTable feeTable;
    private DefaultTableModel tableModel;
    private Long currentStudentId;

    public StudentFeeDialog(Window owner) {
        super(owner, "Thông tin học phí", ModalityType.APPLICATION_MODAL);
        
        // Retrieve logged in student
        currentStudentId = UserSession.getStudentId();
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(owner, "Vui lòng đăng nhập với tư cách Học viên để xem học phí.");
            dispose();
            return;
        }

        initComponents();
        loadFeeData();
    }

    private void initComponents() {
        setSize(800, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Lịch sử nộp Học phí & Hoá đơn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Table setup
        String[] columns = {"Mã Hóa đơn", "Ngày lập", "Tổng tiền", "Ghi chú", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        feeTable = new JTable(tableModel);
        feeTable.setRowHeight(30);
        feeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        feeTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        feeTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        feeTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        feeTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(feeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadFeeData() {
        try {
            List<Invoice> invoices = financeService.getInvoicesByStudentId(currentStudentId);
            tableModel.setRowCount(0);

            if (invoices == null || invoices.isEmpty()) {
                tableModel.addRow(new Object[]{"", "", "Chưa có dữ liệu học phí", "", ""});
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("vi").setRegion("VN").build());

            for (Invoice invoice : invoices) {
                String dateStr = invoice.getIssueDate() != null ? invoice.getIssueDate().format(dateFormatter) : "";
                String amountStr = invoice.getTotalAmount() != null ? currencyFormat.format(invoice.getTotalAmount()) : "0 đ";
                String note = invoice.getNote() != null ? invoice.getNote() : "";

                tableModel.addRow(new Object[]{
                        "HD-" + invoice.getInvoiceId(),
                        dateStr,
                        amountStr,
                        note,
                        translateStatus(invoice.getStatus().name())
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu học phí: " + ex.getMessage());
        }
    }

    private String translateStatus(String status) {
        switch (status) {
            case "Paid":
                return "Đã thanh toán";
            case "Issued":
                return "Chưa thanh toán";
            case "Draft":
                return "Bản nháp";
            case "Cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }
}
