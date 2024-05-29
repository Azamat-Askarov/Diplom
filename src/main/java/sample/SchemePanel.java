package sample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

public class SchemePanel extends JPanel implements MouseWheelListener {
    private List<Integer> amplifierDistances;
    private int distance;
    private int kchopDistance;
    private double scaleFactor = 1.0;

    public SchemePanel(List<Integer> amplifierDistances, int distance, int kchopDistance) {
        this.amplifierDistances = amplifierDistances;
        this.distance = distance;
        this.kchopDistance = kchopDistance;
        addMouseWheelListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(scaleFactor, scaleFactor);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Times New Roman", Font.BOLD, 14 ));

        // Multiplekser va demultiplekserlar
        int width = getWidth();
        int height = getHeight();
        int rectWidth = width / 20; // Gorizontal o'lchamni yana qisqartiramiz
        int rectHeight = height / 5;
        int arrowWidth = width / 20;
        int arrowHeight = height / 15;
        int startX = 10 + (width / 20);
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
            drawAmplifier(g, currentX - segmentLength + 40, lineY, arrowWidth, arrowHeight, i + 1);
            g.drawString(amplifierDistances.get(i) + " km", currentX - segmentLength / 2, lineY + 20);
        }

        // Aylanakchop
        if (kchopDistance > 0) {  // Shunchaki masofa haqiqiy bo'lsa
            int kchopX = rectWidth + startX + (endX - startX - rectWidth) * kchopDistance / distance;
            int kchopHeight = height / 8; // Chiziqning bo'yi (uzunligi)
            drawKchop(g, kchopX-23, lineY -45, rectWidth / 2, kchopHeight); // Aylanakchopni chizish
            g.drawString("KCHOP", kchopX-40, lineY + 50);
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

    private void drawKchop(Graphics g, int x, int y, int width, int height) {
        g.drawRect(x, y + height / 4, width, height / 2); // Pastroqqa tushiramiz
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            scaleFactor += -e.getUnitsToScroll() * 0.1;
            scaleFactor = Math.max(0.1, scaleFactor); // Prevent scaleFactor from becoming too small
            revalidate();
            repaint();
        }
    }
}
