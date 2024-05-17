import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.*;
import java.util.Vector;
import java.awt.print.Printable;

public class PrintForm extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> tableSelector;
    
    public PrintForm() {
        setTitle("Режим печати");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableSelector = new JComboBox<>(new String[] {"Учащиеся", "Преподаватели"});
        tableSelector.addActionListener(e -> loadTableData((String) tableSelector.getSelectedItem()));
        add(tableSelector, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton printButton = new JButton("Печать");
        printButton.addActionListener(this::printTableData);
        add(printButton, BorderLayout.SOUTH);

       
        loadTableData((String) tableSelector.getSelectedItem());
    }

    private void loadTableData(String tableName) {
        String query = "SELECT * FROM " + tableName;
        try (Connection conn = DriverManager.getConnection(Main.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            tableModel.setRowCount(0); 
            tableModel.setColumnIdentifiers(getColumnNames(rs));
            
            while (rs.next()) {
                tableModel.addRow(getRowData(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private Vector<String> getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i));
        }
        return columnNames;
    }
    
    private Vector<Object> getRowData(ResultSet rs) throws SQLException {
        Vector<Object> rowData = new Vector<>();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            rowData.add(rs.getObject(i));
        }
        return rowData;
    }
    
    private void printTableData(ActionEvent event) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Печать данных таблицы");
        
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE; 
            }
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            table.printAll(g2d);
            return Printable.PAGE_EXISTS; 
        });
        
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка печати: " + ex.getMessage(), "Ошибка печати", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrintForm().setVisible(true));
    }
}