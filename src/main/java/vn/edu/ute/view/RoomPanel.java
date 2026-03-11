package vn.edu.ute.view;

import vn.edu.ute.model.Branch;
import vn.edu.ute.model.Room;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.impl.BranchServiceImpl;
import vn.edu.ute.service.impl.RoomServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JPanel {

    private final RoomService roomService = new RoomServiceImpl();
    private final BranchService branchService = new BranchServiceImpl();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtRoomName, txtCapacity, txtLocation, txtSearch;
    private JComboBox<Branch> cbBranch;
    private JComboBox<Status> cbStatus;

    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnSearch;

    public RoomPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataToTable(roomService.getAllRooms());
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Phòng học"));

        txtRoomName = new JTextField();
        txtCapacity = new JTextField("0");
        txtLocation = new JTextField();

        cbBranch = new JComboBox<>();
        loadActiveBranches();
        cbBranch.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Branch) {
                    setText(((Branch) value).getBranchName());
                }
                return this;
            }
        });

        cbStatus = new JComboBox<>(Status.values());

        formPanel.add(new JLabel("Tên phòng:"));
        formPanel.add(txtRoomName);
        formPanel.add(new JLabel("Sức chứa:"));
        formPanel.add(txtCapacity);

        formPanel.add(new JLabel("Vị trí:"));
        formPanel.add(txtLocation);
        formPanel.add(new JLabel("Chi nhánh:"));
        formPanel.add(cbBranch);

        formPanel.add(new JLabel("Trạng thái:"));
        formPanel.add(cbStatus);
        
        // Trống để căn chỉnh
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Tên Phòng", "Sức Chứa", "Vị Trí", "Chi Nhánh", "Trạng Thái"};
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
                Long roomId = (Long) tableModel.getValueAt(selectedRow, 0);

                Room room = roomService.getAllRooms().stream()
                        .filter(r -> r.getRoomId().equals(roomId))
                        .findFirst()
                        .orElse(null);

                if (room != null) {
                    txtRoomName.setText(room.getRoomName());
                    txtCapacity.setText(String.valueOf(room.getCapacity()));
                    txtLocation.setText(room.getLocation());
                    
                    if (room.getBranch() != null) {
                        setSelectedItemById(cbBranch, room.getBranch().getBranchId());
                    }
                    
                    cbStatus.setSelectedItem(room.getStatus());
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
        searchPanel.add(new JLabel("Tìm tên phòng:"));
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

    private void loadDataToTable(List<Room> rooms) {
        tableModel.setRowCount(0);
        if (rooms != null) {
            for (Room r : rooms) {
                tableModel.addRow(new Object[]{
                        r.getRoomId(),
                        r.getRoomName(),
                        r.getCapacity(),
                        r.getLocation(),
                        r.getBranch() != null ? r.getBranch().getBranchName() : "",
                        r.getStatus().name()
                });
            }
        }
    }

    private void loadActiveBranches() {
        cbBranch.removeAllItems();
        List<Branch> activeBranches = branchService.getActiveBranches();
        if (activeBranches != null) {
            for (Branch b : activeBranches)
                cbBranch.addItem(b);
        }
    }

    private void setSelectedItemById(JComboBox<?> comboBox, Long id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Object item = comboBox.getItemAt(i);
            if (item instanceof Branch && ((Branch) item).getBranchId().equals(id)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void setupButtonListeners() {
        btnAdd.addActionListener(e -> {
            try {
                Room room = Room.builder()
                        .roomName(txtRoomName.getText())
                        .capacity(Integer.parseInt(txtCapacity.getText()))
                        .location(txtLocation.getText())
                        .branch((Branch) cbBranch.getSelectedItem())
                        .status((Status) cbStatus.getSelectedItem())
                        .build();

                roomService.addRoom(room);
                JOptionPane.showMessageDialog(this, "Thêm phòng học thành công!");
                loadDataToTable(roomService.getAllRooms());
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm phòng học. Vui lòng kiểm tra lại thông tin!");
            }
        });

        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng học cần cập nhật!");
                return;
            }
            try {
                Long roomId = (Long) tableModel.getValueAt(selectedRow, 0);

                Room room = Room.builder()
                        .roomId(roomId)
                        .roomName(txtRoomName.getText())
                        .capacity(Integer.parseInt(txtCapacity.getText()))
                        .location(txtLocation.getText())
                        .branch((Branch) cbBranch.getSelectedItem())
                        .status((Status) cbStatus.getSelectedItem())
                        .build();

                roomService.updateRoom(room);
                JOptionPane.showMessageDialog(this, "Cập nhật phòng học thành công!");
                loadDataToTable(roomService.getAllRooms());
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật phòng. Vui lòng kiểm tra lại!");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long roomId = (Long) tableModel.getValueAt(selectedRow, 0);
                    roomService.removeRoom(roomId);
                    JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
                    loadDataToTable(roomService.getAllRooms());
                    clearForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu! Có thể phòng đang được sử dụng.");
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            clearForm();
            loadActiveBranches();
            loadDataToTable(roomService.getAllRooms());
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            List<Room> result = roomService.searchRoomByName(keyword);
            loadDataToTable(result);
        });
    }

    private void clearForm() {
        txtRoomName.setText("");
        txtCapacity.setText("0");
        txtLocation.setText("");
        if (cbBranch.getItemCount() > 0) cbBranch.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtSearch.setText("");
        table.clearSelection();
    }
}
