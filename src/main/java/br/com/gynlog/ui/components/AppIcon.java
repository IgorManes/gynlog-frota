package br.com.gynlog.ui.components;

import br.com.gynlog.ui.Theme;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class AppIcon implements Icon {
    public enum Type { HOME, VEHICLE, EXPENSE, MOVEMENT, REPORT, EXPORT, ADD, EDIT, DELETE, SEARCH }

    private final Type type;
    private final Color color;
    private final int size;

    public AppIcon(Type type, Color color, int size) {
        this.type = type;
        this.color = color;
        this.size = size;
    }

    public static AppIcon action(Type type) {
        return new AppIcon(type, Color.WHITE, 16);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(Math.max(1.5f, size / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int m = Math.max(2, size / 5);
        switch (type) {
            case ADD -> {
                g.drawLine(x + size / 2, y + m, x + size / 2, y + size - m);
                g.drawLine(x + m, y + size / 2, x + size - m, y + size / 2);
            }
            case EDIT -> {
                g.drawLine(x + m, y + size - m, x + size - m, y + m);
                g.drawLine(x + m, y + size - m, x + m + 4, y + size - m - 1);
            }
            case DELETE -> {
                g.drawRect(x + m + 1, y + m + 3, size - (m * 2) - 2, size - (m * 2) - 1);
                g.drawLine(x + m, y + m, x + size - m, y + m);
            }
            case SEARCH -> {
                g.drawOval(x + m, y + m, size / 2, size / 2);
                g.drawLine(x + size / 2 + 2, y + size / 2 + 2, x + size - m, y + size - m);
            }
            case HOME -> {
                g.drawLine(x + m, y + size / 2, x + size / 2, y + m);
                g.drawLine(x + size / 2, y + m, x + size - m, y + size / 2);
                g.drawRect(x + m + 2, y + size / 2, size - (m * 2) - 4, size / 2 - m);
            }
            case VEHICLE -> {
                g.drawRect(x + m, y + size / 2 - 3, size - m * 2, size / 3);
                g.drawOval(x + m + 1, y + size - m - 2, 3, 3);
                g.drawOval(x + size - m - 5, y + size - m - 2, 3, 3);
            }
            case EXPENSE -> {
                g.drawOval(x + m, y + m, size - m * 2, size - m * 2);
                g.drawLine(x + size / 2, y + m + 2, x + size / 2, y + size - m - 2);
            }
            case MOVEMENT -> {
                g.drawLine(x + m, y + size / 3, x + size - m, y + size / 3);
                g.drawLine(x + size - m - 3, y + size / 3 - 3, x + size - m, y + size / 3);
                g.drawLine(x + m, y + size * 2 / 3, x + size - m, y + size * 2 / 3);
            }
            case REPORT -> {
                g.drawRect(x + m, y + m, size - m * 2, size - m * 2);
                g.drawLine(x + m + 3, y + size - m - 3, x + size / 2, y + size / 2);
                g.drawLine(x + size / 2, y + size / 2, x + size - m - 3, y + m + 4);
            }
            case EXPORT -> {
                g.drawLine(x + size / 2, y + m, x + size / 2, y + size - m - 3);
                g.drawLine(x + size / 2, y + size - m - 3, x + m + 2, y + size / 2);
                g.drawLine(x + size / 2, y + size - m - 3, x + size - m - 2, y + size / 2);
            }
        }
        g.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
