import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class SearchingForm extends JFrame {
    private JComboBox<String> categorySelector;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> sortSelector; // Выбор поля для сортировки
    private JButton sortButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public SearchingForm() {
        setTitle("Поиск и сортировка данных");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        categorySelector = new JComboBox<>(new String[]{
            "Фамилия Учащихся", 
            "Год рождения Учащихся", 
            "Фамилия Преподавателей", 
            "Кафедра Преподавателей",
            "Предмет Преподавателей"
        });
        searchPanel.add(new JLabel("Выберите категорию:"));
        searchPanel.add(categorySelector);

        searchField = new JTextField(20);
        searchPanel.add(new JLabel("Поиск:"));
        searchPanel.add(searchField);

        searchButton = new JButton("Поиск");
        searchButton.addActionListener(this::performSearch);
        searchPanel.add(searchButton);

        sortSelector = new JComboBox<>(new String[]{"Фамилия", "Кафедра", "Предмет"}); 
        searchPanel.add(new JLabel("Сортировать по:"));
        searchPanel.add(sortSelector);

        sortButton = new JButton("Сортировать");
        sortButton.addActionListener(this::performSort);
        searchPanel.add(sortButton);

        add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        resultsTable = new JTable(tableModel);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
    }

    private void performSearch(ActionEvent e) {
        String selectedCategory = (String) categorySelector.getSelectedItem();
        String searchText = searchField.getText().trim();
        String query = "";
        String tableName = selectedCategory.contains("Учащихся") ? "Учащиеся" : "Преподаватели";

        try (Connection conn = DriverManager.getConnection(Main.DB_URL)) {
            if (selectedCategory.contains("Фамилия")) {
                query = "SELECT * FROM " + tableName + " WHERE фамилия LIKE ?";
            } else if (selectedCategory.contains("Год рождения")) {
                query = "SELECT * FROM " + tableName + " WHERE год_рождения = ?";
            } else if (selectedCategory.contains("Кафедра")) {
                query = "SELECT * FROM " + tableName + " WHERE кафедра LIKE ?";
            } else if (selectedCategory.contains("Предмет")) {
                query = "SELECT * FROM " + tableName + " WHERE предмет LIKE ?";
            }
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            if (query.contains("LIKE")) {
                pstmt.setString(1, "%" + searchText + "%");
            } else {
                pstmt.setInt(1, Integer.parseInt(searchText)); 
            }
            executeSearch(pstmt);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Неверный формат числа для года рождения.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка выполнения запроса: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    private void performSort(ActionEvent e) {
        String selectedField = (String) sortSelector.getSelectedItem();
        String tableName = "Преподаватели"; 
    
        String query = "SELECT * FROM " + tableName + " ORDER BY ";
        if ("Фамилия".equals(selectedField)) {
            query += "\"фамилия\"";
        } else if ("Кафедра".equals(selectedField)) {
            query += "\"кафедра\"";
        } else if ("Предмет".equals(selectedField)) {
            query += "\"предмет\"";
        }
        query += " ASC"; // Добавляем направление сортировки
    
        try (Connection conn = DriverManager.getConnection(Main.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
    
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
    
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }
    
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка выполнения запроса сортировки: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void executeSearch(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

       
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }
        tableModel.setColumnIdentifiers(columnNames);

        // Заполнение данных
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchingForm().setVisible(true));
    }
}
