package sample;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DiagramPanel extends JPanel {
    private List<Integer> amplifierDistances;
    private List<Double> noiseLevels;
    private List<Double> noisePowers;
    private Double multiplexerPower;

    public DiagramPanel(List<Integer> amplifierDistances, List<Double> noiseLevels, List<Double> noisePowers, double multiplexerPower) {
        this.amplifierDistances = amplifierDistances;
        this.noiseLevels = noiseLevels;
        this.noisePowers = noisePowers;
        this.multiplexerPower = multiplexerPower;
        setPreferredSize(new Dimension(800, 400)); // Set preferred size for the diagram panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int margin = 50;

        // Define Y-axis range
        double minSignalLevel = -40.0;
        double maxSignalLevel = 30.0;

        // Calculate max distance for scaling X coordinate
        int totalDistance = amplifierDistances.stream().mapToInt(Integer::intValue).sum();

        // Calculate the Y position for the zero point (origin)
        int zeroY = getYCoordinate(height, margin, maxSignalLevel, minSignalLevel, 0);

        // Draw axes
        g2.drawLine(margin, zeroY, width - margin, zeroY); // X o'qi (nol nuqtadan)
        g2.drawLine(margin, margin, margin, height - margin); // Y o'qi

        // Draw labels
        g2.drawString("Psig, Рsh dB", 10, margin - 10); // Y o'qi sarlavhasi

        // Draw Y-axis labels
        for (double i = minSignalLevel; i <= maxSignalLevel; i += 10) {
            int y = getYCoordinate(height, margin, maxSignalLevel, minSignalLevel, i);
            g2.drawString(String.format("%.0f", i), 5, y + 5);
            g2.drawLine(margin - 5, y, margin + 5, y);
        }

        // Draw X-axis labels
        int cumulativeDistance = 0;
        for (int distance : amplifierDistances) {
            cumulativeDistance += distance;
            int x = margin + (width - 2 * margin) * cumulativeDistance / totalDistance;
            g2.drawString(String.format("%d", cumulativeDistance), x - 30, zeroY + 20); // Shifted 20 pixels left
            g2.drawLine(x, zeroY - 5, x, zeroY + 5);
        }

        int x = margin;
        int prevX = x;
        int prevY = getYCoordinate(height, margin, maxSignalLevel, minSignalLevel, multiplexerPower);

        // Draw data points and lines
        for (int i = 0; i < amplifierDistances.size(); i++) {
            // Calculate X coordinate based on distance
            x += (int) ((width - 2 * margin) * ((double) amplifierDistances.get(i) / totalDistance));
            // Calculate Y coordinate based on noise level
            int y = getYCoordinate(height, margin, maxSignalLevel, minSignalLevel, noiseLevels.get(i));

            // Draw line from previous point to current point
            g2.drawLine(prevX, prevY, x, y);
            prevX = x;

            // Signal drops to noise level at amplifier distance
            prevY = y;
            // Draw point
            g2.fillOval(x - 3, y - 3, 6, 6);
            // Draw value labels
            g2.drawString(String.format("%.1f dBq", noiseLevels.get(i)), x - 20, y + 15); // Adjusted to 25 pixels below

            // Signal boosts back to multiplexer power
            y = getYCoordinate(height, margin, maxSignalLevel, minSignalLevel, multiplexerPower);
            g2.drawLine(prevX, prevY, x, y);
            prevY = y;
        }

        // Draw starting multiplexer power value
        g2.drawString(String.format("%.1f dBq", multiplexerPower), margin - 20, getYCoordinate(height, margin, maxSignalLevel, minSignalLevel, multiplexerPower) + 15); // Adjusted to 25 pixels below

        // Draw X-axis label at the end
        g2.drawString("Lkuch km", width - margin, zeroY + 8); // Shifted 20 pixels to the right and 20 pixels up

        // Set font for the title
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth("Optik kanalning sath diagrammasi");
        // Draw the title
        int titleX = (width - titleWidth) / 2;
        int titleY = margin / 2 + fm.getAscent() / 2;
        g2.drawString("Optik kanalning sath diagrammasi", titleX, titleY);
    }

    private int getYCoordinate(int height, int margin, double maxSignalLevel, double minSignalLevel, double signalLevel) {
        return height - margin - (int) ((height - 2 * margin) * ((signalLevel - minSignalLevel) / (maxSignalLevel - minSignalLevel)));
    }
}
