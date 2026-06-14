package br.com.gynlog.ui.components;

import br.com.gynlog.ui.Theme;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class BrandLogo extends JComponent {
    private static final String LOGO_RESOURCE = "/br/com/gynlog/ui/components/Logo.png";
    private static final BufferedImage LOGO_IMAGE = loadLogo();

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
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (LOGO_IMAGE == null) {
            paintFallback(g);
        } else {
            paintLogo(g);
        }
        g.dispose();
    }

    private void paintLogo(Graphics2D g) {
        double scale = Math.min(
                getWidth() / (double) LOGO_IMAGE.getWidth(),
                getHeight() / (double) LOGO_IMAGE.getHeight());
        int width = Math.max(1, (int) Math.round(LOGO_IMAGE.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(LOGO_IMAGE.getHeight() * scale));
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;
        g.drawImage(LOGO_IMAGE, x, y, width, height, null);
    }

    private void paintFallback(Graphics2D g) {
        int fontSize = compact ? 29 : 57;
        String name = "GYNLOG";
        g.setColor(darkBackground ? Color.WHITE : Theme.NAVY);
        g.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD | Font.ITALIC, fontSize));
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(name)) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(name, x, y);
    }

    private static BufferedImage loadLogo() {
        try (InputStream stream = BrandLogo.class.getResourceAsStream(LOGO_RESOURCE)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (IOException exception) {
            return null;
        }
    }
}
