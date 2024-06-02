package sample;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Main extends JFrame {

    private JTextArea resultArea; // Natijalarni ko'rsatish uchun JTextArea
    private JTabbedPane tabbedPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }

    public Main() {
        setTitle("Askarov Azamat. Bitiruv malakaviy ishi.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new ButtonPanel(this), BorderLayout.WEST); // ButtonPanelni chapga qo'shish
        add(createFooterLabel(), BorderLayout.SOUTH);
        add(createTabbedPane(), BorderLayout.CENTER); // Natijalarni ko'rsatish paneli
    }

    private JTabbedPane createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Natijalar", createResultPanel());
        return tabbedPane;
    }

    private JPanel createHeaderPanel() {
        JLabel logoLabel = createLogoLabel();
        JLabel headerLabel = createHeaderLabel();

        JPanel roundedPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRoundedBorder(g, getWidth(), getHeight(), 40, 8, Color.GREEN);
            }
        };

        roundedPanel.setOpaque(false);
        roundedPanel.setBackground(Color.WHITE);
        roundedPanel.setForeground(Color.GREEN);
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        roundedPanel.add(headerLabel, BorderLayout.CENTER);

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.add(logoLabel, BorderLayout.WEST);
        logoPanel.add(roundedPanel, BorderLayout.CENTER);
        logoPanel.setBorder(new LineBorder(Color.GREEN, 1, true));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(logoPanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JLabel createLogoLabel() {
        ImageIcon originalLogoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/sample/tuit_logo.png")));
        Image originalImage = originalLogoIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaledImage));
    }

    private JLabel createHeaderLabel() {
        JLabel headerLabel = new JLabel("WDM texnologiyali optik transport aloqa tarmog'ini modellashtirish", JLabel.CENTER);
        headerLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(Color.WHITE);
        headerLabel.setForeground(Color.BLACK);
        return headerLabel;
    }

    private JLabel createFooterLabel() {
        JLabel footerLabel = new JLabel("© 411-20 guruh bitiruvchisi Askarov Azamatning Bitiruv Malakaviy Ishi", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        return footerLabel;
    }

    private void drawRoundedBorder(Graphics g, int width, int height, int radius, int borderWidth, Color color) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - 1, height - 1, radius, radius);
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(borderWidth));
        graphics.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);
    }

    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 15));
        resultPanel.setFont(new Font("Arial", Font.PLAIN, 15));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return resultPanel;
    }

    public void displaySimulationResults(int networkLength, int kchopLength, String results, List<Integer> amplifierDistances, int kchopAmplifier, List<Double> noiseLevels, List<Double> noisePowers, int opticalChannelsNum) {
        resultArea.setText(results);

        // Remove old "Diagramma" tab if it exists
        int diagramIndex = tabbedPane.indexOfTab("Diagramma");
        if (diagramIndex != -1) {
            tabbedPane.removeTabAt(diagramIndex);
        }
        // Add new "Diagramma" tab
        DiagramPanel diagramPanel = new DiagramPanel(amplifierDistances, noiseLevels, noisePowers);
        tabbedPane.addTab("Diagramma", diagramPanel);

        // Remove old "Sxema" tab if it exists
        int schemeIndex = tabbedPane.indexOfTab("Sxema");
        if (schemeIndex != -1) {
            tabbedPane.removeTabAt(schemeIndex);
        }
        // Add new "Sxema" tab
        SchemePanel drawScheme = new SchemePanel(amplifierDistances, networkLength, kchopLength, kchopAmplifier, opticalChannelsNum);
        tabbedPane.addTab("Sxema", drawScheme);
    }
}
