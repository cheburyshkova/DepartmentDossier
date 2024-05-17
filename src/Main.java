import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Main {
    public static final String DB_URL = "jdbc:sqlite:C:/Users/superbegemot/Desktop/DepartmentDossier/deb.db";
    private static JTextField txtSurname, txtName, txtPatronymic, txtYear, txtPayment, txtAverage;
    private static JComboBox<String> cbGender;
    private static JComboBox<Integer> cbGrade1, cbGrade2, cbGrade3, cbGrade4;
    private static JTable studentsTable, teachersTable;
    private static DefaultTableModel studentsTableModel, teachersTableModel;

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Не удалось загрузить драйвер SQLite.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Ввод и редактирование данных");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        studentsTableModel = new DefaultTableModel();
        studentsTable = createTable(studentsTableModel);
        createTab(tabbedPane, "Учащиеся", studentsTableModel, studentsTable);

        teachersTableModel = new DefaultTableModel();
        teachersTable = createTable(teachersTableModel);
        createTab(tabbedPane, "Преподаватели", teachersTableModel, teachersTable);

        tabbedPane.addTab("Редактирование", createEditPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(1000, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JTable createTable(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                txtSurname.setText(tableModel.getValueAt(selectedRow, 1).toString());
               
            }
        });
        return table;
    }

    private static void createTab(JTabbedPane tabbedPane, String tableName, DefaultTableModel tableModel, JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        tabbedPane.addTab(tableName, scrollPane);
        loadTableData(tableModel, tableName);
    }

    private static void loadTableData(DefaultTableModel tableModel, String tableName) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(getColumnNames(rs));

            while (rs.next()) {
                tableModel.addRow(getRowData(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Vector<String> getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i));
        }
        return columnNames;
    }

    private static Vector<Object> getRowData(ResultSet rs) throws SQLException {
        Vector<Object> rowData = new Vector<>();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            rowData.add(rs.getObject(i));
        }
        return rowData;
    }

    private static JPanel createEditPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Редактирование"));

       
        txtSurname = new JTextField();
        txtName = new JTextField();
        txtPatronymic = new JTextField();
        txtYear = new JTextField();
        txtPayment = new JTextField();
        txtAverage = new JTextField();
        cbGender = new JComboBox<>(new String[]{"М", "Ж"});
        cbGrade1 = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        cbGrade2 = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        cbGrade3 = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        cbGrade4 = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        
        addToPanel(panel, "Фамилия:", txtSurname);
        addToPanel(panel, "Имя:", txtName);
        addToPanel(panel, "Отчество:", txtPatronymic);
        addToPanel(panel, "Год рождения:", txtYear);
        addToPanel(panel, "Пол:", cbGender);
        addToPanel(panel, "Оценка 1:", cbGrade1);
        addToPanel(panel, "Оценка 2:", cbGrade2);
        addToPanel(panel, "Оценка 3:", cbGrade3);
        addToPanel(panel, "Оценка 4:", cbGrade4);
        addToPanel(panel, "Оплата:", txtPayment);
        addToPanel(panel, "Средний балл:", txtAverage);

        JButton btnSave = new JButton("Сохранить");
        btnSave.addActionListener(e -> saveData());
        panel.add(btnSave);

        JButton btnAdd = new JButton("Добавить запись");
        btnAdd.addActionListener(e -> clearForm());
        panel.add(btnAdd);

        JButton btnDelete = new JButton("Удалить запись");
        btnDelete.addActionListener(e -> deleteSelectedRecord());
        panel.add(btnDelete);

        return panel;
    }

    private static void addToPanel(JPanel panel, String label, Component component) {
        panel.add(new JLabel(label));
        panel.add(component);
    }

    private static void saveData() {
        
        String sql = "INSERT INTO Учащиеся (Фамилия, Имя, Отчество, Год_рождения, Пол, Оценка_1, Оценка_2, Оценка_3, Оценка_4, Оплата, Средний_балл) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtSurname.getText());
           
            pstmt.executeUpdate();

  
            loadTableData(studentsTableModel, "Учащиеся");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка сохранения данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearForm() {
        
        txtSurname.setText("");
        txtName.setText("");
  
    }

    private static void deleteSelectedRecord() {
        
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Object id = studentsTableModel.getValueAt(selectedRow, 0); 
            String sql = "DELETE FROM Учащиеся WHERE ID = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, id);
                pstmt.executeUpdate();

              
                loadTableData(studentsTableModel, "Учащиеся");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Ошибка удаления записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Выберите запись для удаления.", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
