package br.com.gynlog.ui.components;

import br.com.gynlog.ui.Theme;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class BrandLogo extends JComponent {
    private final boolean compact;
    private final boolean darkBackground;

    public BrandLogo(boolean compact, boolean darkBackground) {
        this.compact = compact;
        this.darkBackground = darkBackground;
        setOpaque(false);
        setPreferredSize(compact ? new Dimension(210, 82) : new Dimension(430, 180));
        setMinimumSize(getPreferredSize());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double baseWidth = compact ? 210.0 : 430.0;
        double baseHeight = compact ? 82.0 : 180.0;
        double scale = Math.min(getWidth() / baseWidth, getHeight() / baseHeight);
        double offsetX = (getWidth() - baseWidth * scale) / 2;
        double offsetY = (getHeight() - baseHeight * scale) / 2;
        g.translate(offsetX, offsetY);
        g.scale(scale, scale);

        if (compact) {
            paintCompact(g);
        } else {
            paintFull(g);
        }
        g.dispose();
    }

    private void paintCompact(Graphics2D g) {
        paintMark(g, 0, 2, 72);
        paintName(g, 77, 15, 29);
        g.setColor(darkBackground ? new Color(220, 225, 232) : Theme.MUTED);
        g.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 7));
        g.drawString("FINANCAS E TRANSPORTE", 80, 58);
        g.setColor(Theme.PRIMARY);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(80, 66, 116, 66);
    }

    private void paintFull(Graphics2D g) {
        paintMark(g, 155, 2, 120);
        paintName(g, 16, 95, 57);
        g.setColor(darkBackground ? Color.WHITE : Theme.NAVY);
        g.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        String caption = "FINANCAS E TRANSPORTE";
        FontMetrics metrics = g.getFontMetrics();
        int captionX = (430 - metrics.stringWidth(caption)) / 2;
        g.drawString(caption, captionX, 168);
        g.setColor(Theme.PRIMARY);
        g.setStroke(new BasicStroke(3f));
        g.drawLine(captionX - 55, 164, captionX - 14, 164);
        g.drawLine(captionX + metrics.stringWidth(caption) + 14, 164,
                captionX + metrics.stringWidth(caption) + 55, 164);
    }

    private void paintMark(Graphics2D g, int x, int y, int size) {
        g.setColor(Theme.PRIMARY);
        g.fill(new RoundRectangle2D.Double(x + size * .23, y, size * .67, size * .30,
                size * .18, size * .18));
        Path2D orange = new Path2D.Double();
        orange.moveTo(x + size * .68, y + size * .31);
        orange.lineTo(x + size * .95, y + size * .31);
        orange.lineTo(x + size * .82, y + size * .73);
        orange.quadTo(x + size * .76, y + size * .89, x + size * .57, y + size * .87);
        orange.lineTo(x + size * .70, y + size * .72);
        orange.closePath();
        g.fill(orange);

        g.setColor(darkBackground ? Color.WHITE : Theme.NAVY);
        Path2D road = new Path2D.Double();
        road.moveTo(x + size * .10, y + size * .88);
        road.quadTo(x + size * .35, y + size * .48, x + size * .75, y + size * .40);
        road.quadTo(x + size * .43, y + size * .60, x + size * .29, y + size * .88);
        road.closePath();
        g.fill(road);
        g.setColor(darkBackground ? Theme.NAVY : Color.WHITE);
        for (int index = 0; index < 3; index++) {
            double px = x + size * (.37 + index * .10);
            double py = y + size * (.72 - index * .09);
            Polygon stripe = new Polygon(
                    new int[]{(int) px, (int) (px + size * .06), (int) (px + size * .10), (int) (px + size * .04)},
                    new int[]{(int) py, (int) (py - size * .03), (int) (py - size * .07), (int) (py - size * .04)}, 4);
            g.fill(stripe);
        }
    }

    private void paintName(Graphics2D g, int x, int y, int size) {
        Font font = new Font(Theme.FONT_FAMILY, Font.BOLD | Font.ITALIC, size);
        g.setFont(font);
        g.setColor(darkBackground ? Color.WHITE : Theme.NAVY);
        g.drawString("GYN", x, y + size);
        int width = g.getFontMetrics().stringWidth("GYN");
        g.setColor(Theme.PRIMARY);
        AffineTransform original = g.getTransform();
        g.shear(-0.04, 0);
        g.drawString("LOG", x + width - 2, y + size);
        g.setTransform(original);
    }
}
