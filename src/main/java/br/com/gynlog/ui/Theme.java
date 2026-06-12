package br.com.gynlog.ui;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

public final class Theme {
    private static final ThemeCss CSS = ThemeCss.load();

    public static final Color NAVY = CSS.color("brand-navy");
    public static final Color NAVY_LIGHT = NAVY.brighter();
    public static final Color PRIMARY = CSS.color("brand-orange");
    public static final Color PRIMARY_LIGHT = CSS.color("brand-orange-light");
    public static final Color GOLD = CSS.color("brand-gold");
    public static final Color PRIMARY_DARK = PRIMARY.darker();
    public static final Color SUCCESS = CSS.color("success");
    public static final Color DANGER = CSS.color("danger");
    public static final Color BACKGROUND = CSS.color("background");
    public static final Color SURFACE = CSS.color("surface");
    public static final Color SURFACE_SOFT = CSS.color("surface-soft");
    public static final Color TEXT = CSS.color("text");
    public static final Color MUTED = CSS.color("muted");
    public static final Color BORDER = CSS.color("border");
    public static final String FONT_FAMILY = CSS.text("font-family");
    public static final int FONT_SIZE = CSS.number("font-size");
    public static final int TITLE_SIZE = CSS.number("title-size");
    public static final int TABLE_ROW_HEIGHT = CSS.number("table-row-height");
    public static final int SIDEBAR_WIDTH = CSS.number("sidebar-width");
    public static final int CARD_PADDING = CSS.number("card-padding");
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
    );

    private Theme() {
    }

    public static void apply() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.font", new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
        UIManager.put("Button.font", new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE - 1));
        UIManager.put("TextField.font", new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
        UIManager.put("ComboBox.font", new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
        UIManager.put("Table.font", new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
        UIManager.put("Table.rowHeight", TABLE_ROW_HEIGHT);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.font", new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE - 1));
        UIManager.put("TableHeader.background", SURFACE_SOFT);
        UIManager.put("Table.selectionBackground", new Color(255, 225, 204));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("OptionPane.messageFont", new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE));
        UIManager.put("OptionPane.buttonFont", new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE - 1));
    }
}
