package br.com.gynlog.ui.views;

import br.com.gynlog.data.AppData;
import br.com.gynlog.model.Movement;
import br.com.gynlog.ui.Theme;
import br.com.gynlog.ui.components.AppIcon;
import br.com.gynlog.ui.components.FormDialogs;
import br.com.gynlog.ui.components.UiFactory;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class ReportsPanel extends JPanel {
    private static final String[] REPORTS = {
            "1. Resumo geral de despesas",
            "2. Despesas por veiculo",
            "3. Despesas por categoria",
            "4. Despesas por periodo",
            "5. Movimentacoes detalhadas",
            "6. Ranking de veiculos por custo",
            "7. Evolucao mensal de despesas",
            "8. Media de despesa por veiculo",
            "9. Veiculos em manutencao",
            "10. Ultimas movimentacoes"
    };

    private final AppData data;
    private final JList<String> reportList = new JList<>(new DefaultListModel<>());
    private final DefaultTableModel previewModel = new DefaultTableModel(new String[]{"Item", "Resultado"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public ReportsPanel(AppData data) {
        this.data = data;
        setLayout(new BorderLayout());
        DefaultListModel<String> listModel = (DefaultListModel<String>) reportList.getModel();
        for (String report : REPORTS) listModel.addElement(report);
        reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportList.setFixedCellHeight(34);
        reportList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) refreshPreview();
        });

        JPanel left = UiFactory.card();
        left.add(new JScrollPane(reportList), BorderLayout.CENTER);
        JPanel right = UiFactory.card();
        JButton generate = UiFactory.button("Gerar relatorio", AppIcon.Type.REPORT, Theme.PRIMARY);
        generate.addActionListener(event -> {
            refreshPreview();
            FormDialogs.info(this, "Previa do relatorio gerada com os dados atuais.");
        });
        right.add(UiFactory.toolbar(generate), BorderLayout.NORTH);
        right.add(UiFactory.tableScroll(new JTable(previewModel)), BorderLayout.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.35);
        split.setDividerSize(8);
        split.setBorder(null);
        add(UiFactory.page("Relatorios", "Lista dos 10 relatorios obrigatorios", split), BorderLayout.CENTER);
        reportList.setSelectedIndex(0);
        data.addListener(this::refreshPreview);
    }

    private void refreshPreview() {
        int selected = reportList.getSelectedIndex();
        if (selected < 0) return;
        previewModel.setRowCount(0);
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        Map<String, BigDecimal> values = new LinkedHashMap<>();
        switch (selected) {
            case 0 -> {
                values.put("Total de veiculos", BigDecimal.valueOf(data.vehicles().size()));
                values.put("Total de movimentacoes", BigDecimal.valueOf(data.movements().size()));
                values.put("Despesas acumuladas", data.totalExpenses());
            }
            case 1, 5 -> data.movements().forEach(movement ->
                    values.merge(movement.vehicle().plate(), movement.value(), BigDecimal::add));
            case 2 -> data.movements().forEach(movement ->
                    values.merge(movement.category().name(), movement.value(), BigDecimal::add));
            case 8 -> data.vehicles().stream().filter(vehicle -> vehicle.status().equals("Manutencao"))
                    .forEach(vehicle -> values.put(vehicle.plate() + " - " + vehicle.model(), BigDecimal.ZERO));
            case 9 -> data.movements().stream().sorted((a, b) -> b.date().compareTo(a.date())).limit(10)
                    .forEach(movement -> values.put(movement.date() + " - " + movement.description(), movement.value()));
            default -> data.movements().stream().collect(Collectors.groupingBy(
                            movement -> movement.date().getMonth() + "/" + movement.date().getYear(),
                            LinkedHashMap::new, Collectors.reducing(BigDecimal.ZERO, Movement::value, BigDecimal::add)))
                    .forEach(values::put);
        }
        values.forEach((label, value) -> previewModel.addRow(new Object[]{
                label, selected == 0 && label.startsWith("Total de") ? value.toPlainString() : currency.format(value)}));
    }
}
