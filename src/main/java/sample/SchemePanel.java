package sample;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SchemePanel extends JPanel {
    private List<Integer> amplifierDistances;
    private int distance;
    private int kchopDistance;

    public SchemePanel(List<Integer> amplifierDistances, int distance, int kchopDistance) {
        this.amplifierDistances = amplifierDistances;
        this.distance = distance;
        this.kchopDistance = kchopDistance;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        // Multiplekser va demultiplekserlar
        int width = getWidth();
        int height = getHeight();
        int rectWidth = width / 20; // Gorizontal o'lchamni yana qisqartiramiz
        int rectHeight = height / 5;
        int arrowWidth = width / 20;
        int arrowHeight = height / 15;
        int startX = width / 20;
        int endX = width - (width / 20) - rectWidth;

        // Multiplekser
        drawTrapezoid(g, startX, (height - rectHeight) / 2, rectWidth, rectHeight, true);
        g.drawString("STM-16", startX, (height - rectHeight) / 2 - 10);
        drawMultiplexerInputs(g, startX, (height - rectHeight) / 2, rectWidth, rectHeight);

        // Demultiplekser
        drawTrapezoid(g, endX, (height - rectHeight) / 2, rectWidth, rectHeight, false);
        g.drawString("STM-16", endX, (height - rectHeight) / 2 - 10);
        drawDemultiplexerOutputs(g, endX, (height - rectHeight) / 2, rectWidth, rectHeight);

        // Chiziq va kuchaytirgichlar
        int lineY = height / 2;
        int currentX = startX + rectWidth;
        g.drawLine(currentX, lineY, endX, lineY);

        for (int i = 0; i < amplifierDistances.size(); i++) {
            int segmentLength = (endX - startX - rectWidth) * amplifierDistances.get(i) / distance;
            currentX += segmentLength;
            drawAmplifier(g, currentX - segmentLength + 60, lineY, arrowWidth, arrowHeight, i + 1);
            g.drawString(amplifierDistances.get(i) + " km", currentX - segmentLength / 2, lineY + 20);
        }

        // Aylanakchop
        if (kchopDistance > 0) {  // Only draw the kchop if the distance is valid
            int antennaX = rectWidth + startX + (endX - startX - rectWidth) * kchopDistance / distance;
            drawAntenna(g, antennaX, lineY, 30); // 30 is the diameter of the antenna
            g.drawString("KCHOP", antennaX - 15, lineY + 50);
        }
    }

    private void drawTrapezoid(Graphics g, int x, int y, int width, int height, boolean isLeft) {
        Polygon trapezoid = new Polygon();
        if (isLeft) {
            trapezoid.addPoint(x, y);
            trapezoid.addPoint(x + width, y + (height / 4));
            trapezoid.addPoint(x + width, y + (height * 3 / 4));
            trapezoid.addPoint(x, y + height);
        } else {
            trapezoid.addPoint(x, y + (height / 4));
            trapezoid.addPoint(x + width, y);
            trapezoid.addPoint(x + width, y + height);
            trapezoid.addPoint(x, y + (height * 3 / 4));
        }
        g.drawPolygon(trapezoid);
    }

    private void drawAmplifier(Graphics g, int x, int y, int width, int height, int number) {
        int[] xPoints = {x - width / 2, x + width / 2, x - width / 2};
        int[] yPoints = {y - height / 2, y, y + height / 2};
        g.drawPolygon(xPoints, yPoints, 3);
        g.drawString(String.valueOf(number), x - 3, y - height / 2 - 5);
    }

    private void drawAntenna(Graphics g, int x, int y, int diameter) {
        g.drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
    }

    private void drawMultiplexerInputs(Graphics g, int x, int y, int width, int height) {
        int lineLength = 20; // Length of the horizontal lines
        int spacing = height / 5; // Space between the lines

        for (int i = 0; i < 4; i++) {
            int lineY = y + (spacing * (i + 1));
            g.drawLine(x - lineLength, lineY, x, lineY);
        }

        // Labeling the inputs
        for (int i = 0; i < 4; i++) {
            int lineY = y + (spacing * (i + 1));
            g.drawString("λ " + (i + 1), x - lineLength - 40, lineY + 5);
        }
    }

    private void drawDemultiplexerOutputs(Graphics g, int x, int y, int width, int height) {
        int lineLength = 20; // Length of the horizontal lines
        int spacing = height / 5; // Space between the lines

        for (int i = 0; i < 4; i++) {
            int lineY = y + (spacing * (i + 1));
            g.drawLine(x + width, lineY, x + width + lineLength, lineY);
        }

        // Labeling the outputs
        for (int i = 0; i < 4; i++) {
            int lineY = y + (spacing * (i + 1));
            g.drawString("λ " + (i + 1), x + width + lineLength + 10, lineY + 5);
        }
    }
}

