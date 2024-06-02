package sample;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DiagramPanel extends JPanel {
    private List<Integer> amplifierDistances;
    private List<Double> noiseLevels;
    private List<Double> noisePowers;

    public DiagramPanel(List<Integer> amplifierDistances, List<Double> noiseLevels, List<Double> noisePowers) {
        this.amplifierDistances = amplifierDistances;
        this.noiseLevels = noiseLevels;
        this.noisePowers = noisePowers;
        setPreferredSize(new Dimension(800, 400)); // Set preferred size for the diagram panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int margin = 150;

        // Draw axes
        g2.drawLine(margin, height - margin, width - margin, height - margin); // X o'qi
        g2.drawLine(margin, margin, margin, height - margin); // Y o'qi

        // Draw labels and ticks
        g2.drawString("Masofa (km)", width / 2, height - 10); // X o'qi sarlavhasi
        g2.drawString("Shovqin sathi (dBq)", 10, margin - 10); // Y o'qi sarlavhasi

        // Find max values for scaling
        int maxDistance = amplifierDistances.stream().mapToInt(Integer::intValue).sum();
        double maxNoiseLevel = noiseLevels.stream().mapToDouble(Double::doubleValue).max().orElse(1);

        int x = margin;
        int y = height - margin;
        int prevX = x;
        int prevY = y;

        // Draw data points and lines
        for (int i = 0; i < amplifierDistances.size(); i++) {
            // Calculate X coordinate based on distance
            x += (int) ((width - 2 * margin) * ((double) amplifierDistances.get(i) / maxDistance));
            // Calculate Y coordinate based on noise level
            y = height - margin - (int) ((height - 2 * margin) * (noiseLevels.get(i) / maxNoiseLevel));

            // Draw point
            g2.fillOval(x - 3, y - 3, 6, 6);
            // Draw line connecting previous point to current point
            g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }


}
