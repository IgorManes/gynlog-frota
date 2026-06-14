package br.com.gynlog.ui.views;

import br.com.gynlog.data.AppData;
import br.com.gynlog.data.EstruturaDados;
import br.com.gynlog.model.Movement;
import br.com.gynlog.model.Vehicle;
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
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ReportsPanel extends JPanel {

    private static final String[] RELATORIOS = {
            "1. Despesas por veiculo",
            "2. Total de despesas da frota no mes",
            "3. Total de combustivel no mes",
            "4. Total de IPVA no ano",
            "5. Veiculos inativos",
            "6. Multas por veiculo no ano",
            "7. Media de despesas por categoria",
            "8. Consumo medio por veiculo",
            "9. Custo medio de IPVA no ano",
            "10. Veiculo de maior e menor custo"
    };

    private final AppData data;

    // Instância da nossa estrutura de dados — usada para ordenação manual no relatório 10
    private final EstruturaDados estrutura = new EstruturaDados();

    private final JList<String> listaRelatorios = new JList<>(new DefaultListModel<>());
    private final DefaultTableModel modeloPrevia = new DefaultTableModel(
            new String[]{"Item", "Resultado"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public ReportsPanel(AppData data) {
        this.data = data;
        setLayout(new BorderLayout());

        DefaultListModel<String> modelo = (DefaultListModel<String>) listaRelatorios.getModel();
        for (String r : RELATORIOS) modelo.addElement(r);
        listaRelatorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaRelatorios.setFixedCellHeight(34);
        listaRelatorios.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) atualizarPrevia();
        });

        JPanel esquerda = UiFactory.card();
        esquerda.add(new JScrollPane(listaRelatorios), BorderLayout.CENTER);

        JPanel direita = UiFactory.card();
        JButton gerar = UiFactory.button("Gerar relatorio", AppIcon.Type.REPORT, Theme.PRIMARY);
        gerar.addActionListener(e -> {
            atualizarPrevia();
            FormDialogs.info(this, "Previa gerada com os dados atuais.");
        });
        direita.add(UiFactory.toolbar(gerar), BorderLayout.NORTH);
        direita.add(UiFactory.tableScroll(new JTable(modeloPrevia)), BorderLayout.CENTER);

        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, esquerda, direita);
        divisor.setResizeWeight(0.35);
        divisor.setDividerSize(8);
        divisor.setBorder(null);

        add(UiFactory.page("Relatorios", "10 relatorios obrigatorios do sistema", divisor), BorderLayout.CENTER);
        listaRelatorios.setSelectedIndex(0);
        data.addListener(this::atualizarPrevia);
    }

    private void atualizarPrevia() {
        int selecionado = listaRelatorios.getSelectedIndex();
        if (selecionado < 0) return;
        modeloPrevia.setRowCount(0);
        switch (selecionado) {
            case 0 -> relatorio1_DespesasPorVeiculo();
            case 1 -> relatorio2_TotalMesAtual();
            case 2 -> relatorio3_CombustivelMes();
            case 3 -> relatorio4_IpvaAno();
            case 4 -> relatorio5_VeiculosInativos();
            case 5 -> relatorio6_MultasPorVeiculo();
            case 6 -> relatorio7_MediaPorCategoria();
            case 7 -> relatorio8_ConsumoMedio();
            case 8 -> relatorio9_CustoMedioIpva();
            case 9 -> relatorio10_MaiorMenorCusto();
        }
    }

    // ─── RELATÓRIO 1 — Despesas por veículo ──────────────────────────────────
    private void relatorio1_DespesasPorVeiculo() {
        NumberFormat moeda = moeda();
        Map<String, BigDecimal> totais = new LinkedHashMap<>();
        for (Movement m : data.movements()) {
            String chave = m.vehicle().plate() + " - " + m.vehicle().model();
            totais.merge(chave, m.value(), BigDecimal::add);
        }
        if (totais.isEmpty()) {
            modeloPrevia.addRow(new Object[]{"Nenhuma movimentacao registrada", "-"});
            return;
        }
        totais.forEach((v, total) -> modeloPrevia.addRow(new Object[]{v, moeda.format(total)}));
    }

    // ─── RELATÓRIO 2 — Total de despesas da frota no mês atual ───────────────
    private void relatorio2_TotalMesAtual() {
        NumberFormat moeda = moeda();
        int mes = java.time.LocalDate.now().getMonthValue();
        int ano = java.time.LocalDate.now().getYear();
        BigDecimal total = BigDecimal.ZERO;
        for (Movement m : data.movements()) {
            if (m.date().getMonthValue() == mes && m.date().getYear() == ano) {
                total = total.add(m.value());
            }
        }
        modeloPrevia.addRow(new Object[]{"Mes de referencia", mes + "/" + ano});
        modeloPrevia.addRow(new Object[]{"Total de despesas", moeda.format(total)});
    }

    // ─── RELATÓRIO 3 — Total de combustível no mês atual ─────────────────────
    private void relatorio3_CombustivelMes() {
        NumberFormat moeda = moeda();
        int mes = java.time.LocalDate.now().getMonthValue();
        int ano = java.time.LocalDate.now().getYear();
        BigDecimal total = BigDecimal.ZERO;
        for (Movement m : data.movements()) {
            if (m.date().getMonthValue() == mes
                    && m.date().getYear() == ano
                    && m.category().name().toLowerCase().contains("combustivel")) {
                total = total.add(m.value());
            }
        }
        modeloPrevia.addRow(new Object[]{"Mes de referencia", mes + "/" + ano});
        modeloPrevia.addRow(new Object[]{"Total com combustivel", moeda.format(total)});
    }

    // ─── RELATÓRIO 4 — Total de IPVA no ano atual ────────────────────────────
    private void relatorio4_IpvaAno() {
        NumberFormat moeda = moeda();
        int ano = java.time.LocalDate.now().getYear();
        BigDecimal total = BigDecimal.ZERO;
        for (Movement m : data.movements()) {
            if (m.date().getYear() == ano
                    && m.category().name().toLowerCase().contains("ipva")) {
                total = total.add(m.value());
            }
        }
        modeloPrevia.addRow(new Object[]{"Ano de referencia", String.valueOf(ano)});
        modeloPrevia.addRow(new Object[]{"Total de IPVA", moeda.format(total)});
    }

    // ─── RELATÓRIO 5 — Veículos inativos ─────────────────────────────────────
    private void relatorio5_VeiculosInativos() {
        List<Vehicle> inativos = new ArrayList<>();
        for (Vehicle v : data.vehicles()) {
            if (v.status().equalsIgnoreCase("Inativo")) {
                inativos.add(v);
            }
        }
        if (inativos.isEmpty()) {
            modeloPrevia.addRow(new Object[]{"Nenhum veiculo inativo encontrado", "-"});
            return;
        }
        for (Vehicle v : inativos) {
            modeloPrevia.addRow(new Object[]{
                    v.plate() + " - " + v.model(), v.brand() + " (" + v.year() + ")"
            });
        }
    }

    // ─── RELATÓRIO 6 — Multas por veículo no ano atual ───────────────────────
    private void relatorio6_MultasPorVeiculo() {
        NumberFormat moeda = moeda();
        int ano = java.time.LocalDate.now().getYear();
        Map<String, BigDecimal> multas = new LinkedHashMap<>();
        for (Movement m : data.movements()) {
            if (m.date().getYear() == ano
                    && m.category().name().toLowerCase().contains("multa")) {
                String chave = m.vehicle().plate() + " - " + m.vehicle().model();
                multas.merge(chave, m.value(), BigDecimal::add);
            }
        }
        if (multas.isEmpty()) {
            modeloPrevia.addRow(new Object[]{"Nenhuma multa registrada em " + ano, "-"});
            return;
        }
        multas.forEach((v, total) -> modeloPrevia.addRow(new Object[]{v, moeda.format(total)}));
    }

    // ─── RELATÓRIO 7 — Média de despesas por categoria ───────────────────────
    private void relatorio7_MediaPorCategoria() {
        NumberFormat moeda = moeda();

        // Agrupa manualmente por categoria usando Map
        Map<String, List<BigDecimal>> porCategoria = new LinkedHashMap<>();
        for (Movement m : data.movements()) {
            String cat = m.category().name();
            porCategoria.computeIfAbsent(cat, k -> new ArrayList<>()).add(m.value());
        }

        if (porCategoria.isEmpty()) {
            modeloPrevia.addRow(new Object[]{"Nenhuma movimentacao registrada", "-"});
            return;
        }

        // Calcula a média de cada categoria manualmente
        for (Map.Entry<String, List<BigDecimal>> entrada : porCategoria.entrySet()) {
            BigDecimal soma = BigDecimal.ZERO;
            for (BigDecimal valor : entrada.getValue()) {
                soma = soma.add(valor);
            }
            BigDecimal media = soma.divide(
                    BigDecimal.valueOf(entrada.getValue().size()), 2, RoundingMode.HALF_UP);
            modeloPrevia.addRow(new Object[]{entrada.getKey(), moeda.format(media)});
        }
    }

    // ─── RELATÓRIO 8 — Consumo médio por veículo ─────────────────────────────────
    private void relatorio8_ConsumoMedio() {
        for (Vehicle v : data.vehicles()) {

            // Coleta todos os abastecimentos do veículo
            List<Movement> todosAbastecimentos = new ArrayList<>();
            for (Movement m : data.movements()) {
                if (m.vehicle().id() == v.id()
                        && m.category().name().toLowerCase().contains("combustivel")) {
                    todosAbastecimentos.add(m);
                }
            }

            // Se não tiver nenhum abastecimento, pula o veículo
            if (todosAbastecimentos.isEmpty()) continue;

            // Soma total gasto com combustível
            BigDecimal totalCombustivel = BigDecimal.ZERO;
            for (Movement m : todosAbastecimentos) {
                totalCombustivel = totalCombustivel.add(m.value());
            }

            // Coleta apenas os que têm quilometragem preenchida
            List<Movement> comKm = new ArrayList<>();
            for (Movement m : todosAbastecimentos) {
                if (m.mileage() > 0) comKm.add(m);
            }

            // Se não tiver pelo menos 2 registros com km, mostra só o total gasto
            if (comKm.size() < 2) {
                modeloPrevia.addRow(new Object[]{
                        v.plate() + " - " + v.model(),
                        "Total: " + moeda().format(totalCombustivel) + " | Sem Km p/ calcular CM"
                });
                continue;
            }

            // Ordena por quilometragem — bubble sort manual
            for (int i = 0; i < comKm.size() - 1; i++) {
                for (int j = 0; j < comKm.size() - 1 - i; j++) {
                    if (comKm.get(j).mileage() > comKm.get(j + 1).mileage()) {
                        Movement temp = comKm.get(j);
                        comKm.set(j, comKm.get(j + 1));
                        comKm.set(j + 1, temp);
                    }
                }
            }

            double kmInicial = comKm.get(0).mileage();
            double kmFinal   = comKm.get(comKm.size() - 1).mileage();
            double distancia = kmFinal - kmInicial;

            // Soma combustível dos abastecimentos intermediários (exceto o primeiro)
            BigDecimal totalIntermediario = BigDecimal.ZERO;
            for (int i = 1; i < comKm.size(); i++) {
                totalIntermediario = totalIntermediario.add(comKm.get(i).value());
            }

            if (totalIntermediario.compareTo(BigDecimal.ZERO) > 0 && distancia > 0) {
                double consumo = distancia / totalIntermediario.doubleValue();
                modeloPrevia.addRow(new Object[]{
                        v.plate() + " - " + v.model(),
                        String.format("%.2f km/R$ | Total: %s", consumo, moeda().format(totalCombustivel))
                });
            } else {
                modeloPrevia.addRow(new Object[]{
                        v.plate() + " - " + v.model(),
                        "Total: " + moeda().format(totalCombustivel) + " | Sem Km p/ calcular CM"
                });
            }
        }
    }

    // ─── RELATÓRIO 9 — Custo médio de IPVA no ano ────────────────────────────
    private void relatorio9_CustoMedioIpva() {
        NumberFormat moeda = moeda();
        int ano = java.time.LocalDate.now().getYear();

        List<BigDecimal> valores = new ArrayList<>();
        for (Movement m : data.movements()) {
            if (m.date().getYear() == ano
                    && m.category().name().toLowerCase().contains("ipva")) {
                valores.add(m.value());
            }
        }

        if (valores.isEmpty()) {
            modeloPrevia.addRow(new Object[]{"Nenhum IPVA registrado em " + ano, "-"});
            return;
        }

        BigDecimal soma = BigDecimal.ZERO;
        for (BigDecimal v : valores) soma = soma.add(v);
        BigDecimal media = soma.divide(BigDecimal.valueOf(valores.size()), 2, RoundingMode.HALF_UP);

        modeloPrevia.addRow(new Object[]{"Ano de referencia", String.valueOf(ano)});
        modeloPrevia.addRow(new Object[]{"Total de registros IPVA", String.valueOf(valores.size())});
        modeloPrevia.addRow(new Object[]{"Custo medio de IPVA", moeda.format(media)});
    }

    // ─── RELATÓRIO 10 — Veículo de maior e menor custo ───────────────────────
    private void relatorio10_MaiorMenorCusto() {
        NumberFormat moeda = moeda();

        // Monta o mapa de custo total por veículo manualmente
        Map<Integer, BigDecimal> totaisPorId = new HashMap<>();
        for (Vehicle v : data.vehicles()) {
            BigDecimal total = BigDecimal.ZERO;
            for (Movement m : data.movements()) {
                if (m.vehicle().id() == v.id()) {
                    total = total.add(m.value());
                }
            }
            totaisPorId.put(v.id(), total);
        }

        if (totaisPorId.isEmpty()) {
            modeloPrevia.addRow(new Object[]{"Nenhum veiculo cadastrado", "-"});
            return;
        }

        /*
         * ORDENAÇÃO MANUAL — Selection Sort via EstruturaDados
         *
         * Em vez de usar Collections.sort ou Stream.sorted,
         * chamamos nossa implementação manual do Selection Sort
         * que ordena a lista de veículos pelo custo total.
         *
         * Isso atende ao requisito de Estrutura de Dados I do PI.
         */
        List<Vehicle> ordenados = estrutura.ordenarVeiculosPorCusto(
                data.vehicles(), totaisPorId, false); // false = maior para menor

        Vehicle maior = ordenados.get(0);
        Vehicle menor = ordenados.get(ordenados.size() - 1);

        modeloPrevia.addRow(new Object[]{"Veiculo de MAIOR custo",
                maior.plate() + " - " + maior.model()
                        + " | " + moeda.format(totaisPorId.get(maior.id()))});
        modeloPrevia.addRow(new Object[]{"Veiculo de MENOR custo",
                menor.plate() + " - " + menor.model()
                        + " | " + moeda.format(totaisPorId.get(menor.id()))});

        modeloPrevia.addRow(new Object[]{"--- Todos os veiculos ---", "---"});
        for (Vehicle v : ordenados) {
            modeloPrevia.addRow(new Object[]{
                    v.plate() + " - " + v.model(), moeda.format(totaisPorId.get(v.id()))
            });
        }
    }

    private NumberFormat moeda() {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }
}