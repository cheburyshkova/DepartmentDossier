import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Досье подготовительного отделения");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(400, 300);
        setLocationRelativeTo(null);

        addHeader();
        addButtons();
    }

    private void addHeader() {
        JLabel header = new JLabel("Досье подготовительного отделения", SwingConstants.CENTER);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setFont(new Font("Arial", Font.BOLD, 16));
        add(header);
        addVerticalSpace(10);
    }

    private void addButtons() {
        add(createStyledButton("Ввод и редактирование данных", this::openEditWindow));
        addVerticalSpace(5);
        add(createStyledButton("Поиск и сортировка данных", this::openSearchWindow));
        addVerticalSpace(5);
        add(createStyledButton("Режим печати", this::openPrintWindow));
        addVerticalSpace(5);
        add(createStyledButton("Выход", e -> System.exit(0)));
    }

    private void addVerticalSpace(int height) {
        add(Box.createRigidArea(new Dimension(0, height)));
    }

    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
        button.addActionListener(listener);
        return button;
    }

    private void openEditWindow(ActionEvent e) {
       
        JOptionPane.showMessageDialog(this, "Перейдите на вкладку редактирование", "Информация", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openSearchWindow(ActionEvent e) {
        
        SearchingForm searchWindow = new SearchingForm();
        
        searchWindow.setVisible(true);
    }

    private void openPrintWindow(ActionEvent e) {
        
        PrintForm printWindow = new PrintForm();
    
        printWindow.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
