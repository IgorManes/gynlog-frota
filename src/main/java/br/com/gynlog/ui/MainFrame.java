package br.com.gynlog.ui;

import br.com.gynlog.data.AppData;
import br.com.gynlog.ui.components.AppIcon;
import br.com.gynlog.ui.components.BrandLogo;
import br.com.gynlog.ui.views.DashboardPanel;
import br.com.gynlog.ui.views.ExpenseTypesPanel;
import br.com.gynlog.ui.views.ExportPanel;
import br.com.gynlog.ui.views.MovementsPanel;
import br.com.gynlog.ui.views.ReportsPanel;
import br.com.gynlog.ui.views.VehiclesPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();

    public MainFrame(AppData data) {
        super("GynLog - Gestao de Frota");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 650));
        setSize(1280, 780);
        setLocationRelativeTo(null);

        cards.add(new DashboardPanel(data, this::showScreen), "Inicio");
        cards.add(new VehiclesPanel(data), "Veiculos");
        cards.add(new ExpenseTypesPanel(data), "Despesas");
        cards.add(new MovementsPanel(data), "Movimentacoes");
        cards.add(new ReportsPanel(data), "Relatorios");
        cards.add(new ExportPanel(data), "Exportacao");

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(cards, BorderLayout.CENTER);
        showScreen("Inicio");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.NAVY);
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 14, 18, 14));

        BrandLogo logo = new BrandLogo(true, true);
        logo.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(20));

        addMenu(sidebar, "Inicio", AppIcon.Type.HOME);
        addMenu(sidebar, "Veiculos", AppIcon.Type.VEHICLE);
        addMenu(sidebar, "Despesas", AppIcon.Type.EXPENSE);
        addMenu(sidebar, "Movimentacoes", AppIcon.Type.MOVEMENT);
        addMenu(sidebar, "Relatorios", AppIcon.Type.REPORT);
        addMenu(sidebar, "Exportacao", AppIcon.Type.EXPORT);
        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("GynLog | Financas e Transporte");
        footer.setForeground(new Color(139, 158, 184));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(footer);
        return sidebar;
    }

    private void addMenu(JPanel sidebar, String name, AppIcon.Type iconType) {
        JButton button = new JButton(name, new AppIcon(iconType, Color.WHITE, 18));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setIconTextGap(12);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.NAVY);
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.addActionListener(event -> showScreen(name));
        menuButtons.put(name, button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(4));
    }

    private void showScreen(String name) {
        cardLayout.show(cards, name);
        menuButtons.forEach((screen, button) ->
                button.setBackground(screen.equals(name) ? Theme.PRIMARY : Theme.NAVY));
    }
}
