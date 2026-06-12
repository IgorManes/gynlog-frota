package br.com.gynlog.ui.components;

import br.com.gynlog.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public final class UiFactory {
    private UiFactory() {
    }

    public static JPanel page(String title, String subtitle, JComponent content) {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, Theme.TITLE_SIZE));
        titleLabel.setForeground(Theme.NAVY);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(Theme.MUTED);
        heading.add(titleLabel, BorderLayout.NORTH);
        heading.add(subtitleLabel, BorderLayout.SOUTH);
        page.add(heading, BorderLayout.NORTH);
        page.add(content, BorderLayout.CENTER);
        return page;
    }

    public static JPanel card() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.SURFACE);
        panel.setBorder(Theme.CARD_BORDER);
        return panel;
    }

    public static JButton button(String text, AppIcon.Type icon, Color color) {
        JButton button = new JButton(text, new AppIcon(icon, Color.WHITE, 15));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 13, 9, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JPanel toolbar(JButton... buttons) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        for (JButton button : buttons) {
            toolbar.add(button);
        }
        return toolbar;
    }

    public static JScrollPane tableScroll(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.SURFACE);
        return scroll;
    }

    public static JTextField field() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(220, 34));
        return field;
    }
}
