package br.com.gynlog.ui.views;

import br.com.gynlog.data.AppData;
import br.com.gynlog.ui.Theme;
import br.com.gynlog.ui.components.UiFactory;
import br.com.gynlog.ui.components.BrandLogo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public final class DashboardPanel extends JPanel {
    private final AppData data;
    private final JPanel stats = new JPanel(new GridLayout(1, 3, 16, 16));

    public DashboardPanel(AppData data, Consumer<String> navigate) {
        this.data = data;
        setLayout(new BorderLayout());
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(stats, BorderLayout.NORTH);

        JPanel welcome = UiFactory.card();
        welcome.setBackground(Theme.SURFACE_SOFT);
        JPanel text = new JPanel(new GridLayout(0, 1, 0, 6));
        text.setOpaque(false);
        JLabel title = new JLabel("Bem-vindo ao painel de controle");
        title.setFont(new Font("SansSerif", Font.BOLD, 19));
        JLabel description = new JLabel("Acompanhe a frota e acesse rapidamente as operacoes principais.");
        description.setForeground(Theme.MUTED);
        text.add(title);
        text.add(description);
        JPanel welcomeHeading = new JPanel(new BorderLayout(20, 0));
        welcomeHeading.setOpaque(false);
        welcomeHeading.add(text, BorderLayout.CENTER);
        welcomeHeading.add(new BrandLogo(true, false), BorderLayout.EAST);
        welcome.add(welcomeHeading, BorderLayout.NORTH);

        JPanel shortcuts = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        shortcuts.setOpaque(false);
        for (String screen : new String[]{"Veiculos", "Despesas", "Movimentacoes", "Relatorios"}) {
            JButton button = new JButton("Abrir " + screen);
            button.addActionListener(event -> navigate.accept(screen));
            shortcuts.add(button);
        }
        welcome.add(shortcuts, BorderLayout.CENTER);
        content.add(welcome, BorderLayout.CENTER);
        add(UiFactory.page("Tela Principal", "Visao geral da operacao da GynLog", content), BorderLayout.CENTER);
        data.addListener(this::refresh);
        refresh();
    }

    private void refresh() {
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        stats.removeAll();
        stats.setOpaque(false);
        stats.add(stat("Veiculos cadastrados", String.valueOf(data.vehicles().size()), Theme.PRIMARY));
        stats.add(stat("Movimentacoes", String.valueOf(data.movements().size()), Theme.SUCCESS));
        stats.add(stat("Despesas acumuladas", currency.format(data.totalExpenses()), Theme.GOLD));
        stats.revalidate();
        stats.repaint();
    }

    private JPanel stat(String label, String value, Color color) {
        JPanel card = UiFactory.card();
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, color),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setForeground(Theme.MUTED);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(labelComponent, BorderLayout.SOUTH);
        return card;
    }
}
