package vn.edu.ute.view;

import vn.edu.ute.model.Branch;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.impl.BranchServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BranchPanel extends JPanel {

    private final BranchService branchService = new BranchServiceImpl();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtBranchId, txtBranchName, txtAddress, txtPhone, txtSearch;
    private JComboBox<Status> cbStatus;

    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnSearch;

    public BranchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataToTable(branchService.getAllBranches());
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Chi nhánh"));

        txtBranchName = new JTextField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();
        cbStatus = new JComboBox<>(Status.values());

        formPanel.add(new JLabel("Tên chi nhánh:"));
        formPanel.add(txtBranchName);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(txtAddress);

        formPanel.add(new JLabel("Số điện thoại:"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Trạng thái:"));
        formPanel.add(cbStatus);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Tên Chi Nhánh", "Địa Chỉ", "Số Điện Thoại", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                Long branchId = (Long) tableModel.getValueAt(selectedRow, 0);

                Branch branch = branchService.getAllBranches().stream()
                        .filter(b -> b.getBranchId().equals(branchId))
                        .findFirst()
                        .orElse(null);

                if (branch != null) {
                    txtBranchName.setText(branch.getBranchName());
                    txtAddress.setText(branch.getAddress());
                    txtPhone.setText(branch.getPhone());
                    cbStatus.setSelectedItem(branch.getStatus());
                }
            }
        });

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel actionPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tìm tên chi nhánh:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        setupButtonListeners();

        return actionPanel;
    }

    private void loadDataToTable(List<Branch> branches) {
        tableModel.setRowCount(0);
        if (branches != null) {
            for (Branch b : branches) {
                tableModel.addRow(new Object[]{
                        b.getBranchId(),
                        b.getBranchName(),
                        b.getAddress(),
                        b.getPhone(),
                        b.getStatus().name()
                });
            }
        }
    }

    private void setupButtonListeners() {
        btnAdd.addActionListener(e -> {
            try {
                Branch branch = Branch.builder()
                        .branchName(txtBranchName.getText())
                        .address(txtAddress.getText())
                        .phone(txtPhone.getText())
                        .status((Status) cbStatus.getSelectedItem())
                        .build();

                branchService.addBranch(branch);
                JOptionPane.showMessageDialog(this, "Thêm chi nhánh thành công!");
                loadDataToTable(branchService.getAllBranches());
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm chi nhánh. Vui lòng kiểm tra lại!");
            }
        });

        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn chi nhánh cần cập nhật!");
                return;
            }
            try {
                Long branchId = (Long) tableModel.getValueAt(selectedRow, 0);

                Branch branch = Branch.builder()
                        .branchId(branchId)
                        .branchName(txtBranchName.getText())
                        .address(txtAddress.getText())
                        .phone(txtPhone.getText())
                        .status((Status) cbStatus.getSelectedItem())
                        .build();

                branchService.updateBranch(branch);
                JOptionPane.showMessageDialog(this, "Cập nhật chi nhánh thành công!");
                loadDataToTable(branchService.getAllBranches());
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật chi nhánh. Vui lòng kiểm tra lại!");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn chi nhánh cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa chi nhánh này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long branchId = (Long) tableModel.getValueAt(selectedRow, 0);
                    branchService.deleteBranch(branchId);
                    JOptionPane.showMessageDialog(this, "Xóa chi nhánh thành công!");
                    loadDataToTable(branchService.getAllBranches());
                    clearForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu! Có thể chi nhánh đang có dữ liệu liên quan.");
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            clearForm();
            loadDataToTable(branchService.getAllBranches());
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            List<Branch> result = branchService.searchBranchByName(keyword);
            loadDataToTable(result);
        });
    }

    private void clearForm() {
        txtBranchName.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        cbStatus.setSelectedIndex(0);
        txtSearch.setText("");
        table.clearSelection();
    }
}
