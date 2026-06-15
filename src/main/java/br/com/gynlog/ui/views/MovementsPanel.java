package br.com.gynlog.ui.views;

import br.com.gynlog.data.AppData;
import br.com.gynlog.data.EstruturaDados;
import br.com.gynlog.model.Movement;
import br.com.gynlog.ui.Theme;
import br.com.gynlog.ui.components.AppIcon;
import br.com.gynlog.ui.components.FormDialogs;
import br.com.gynlog.ui.components.UiFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class MovementsPanel extends JPanel {
    private final AppData data;

    private final EstruturaDados estrutura = new EstruturaDados();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Veiculo", "Categoria", "Descricao", "Data", "Valor", "Km"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<Movement> visibleMovements = List.of();

    public MovementsPanel(AppData data) {
        this.data = data;
        setLayout(new BorderLayout());
        JButton add    = UiFactory.button("Nova",    AppIcon.Type.ADD,    Theme.SUCCESS);
        JButton edit   = UiFactory.button("Editar",  AppIcon.Type.EDIT,   Theme.PRIMARY);
        JButton delete = UiFactory.button("Excluir", AppIcon.Type.DELETE, Theme.DANGER);
        add.addActionListener(event    -> add());
        edit.addActionListener(event   -> edit());
        delete.addActionListener(event -> delete());

        JPanel card = UiFactory.card();
        card.add(UiFactory.toolbar(add, edit, delete), BorderLayout.NORTH);
        card.add(UiFactory.tableScroll(table), BorderLayout.CENTER);
        add(UiFactory.page("Movimentacoes",
                        "Registre veiculo, categoria, descricao, data, valor e quilometragem", card),
                BorderLayout.CENTER);
        data.addListener(this::refresh);
        refresh();
    }

    private void add() {
        FormDialogs.MovementForm form = FormDialogs.movement(
                this, data.vehicles(), data.expenseTypes(), null);
        if (form != null) {
            Movement nova = data.addMovement(
                    form.vehicle(), form.category(), form.description(),
                    form.date(), form.value(), form.mileage());

            if (nova == null) {
                FormDialogs.error(this, "Nao e permitido registrar despesas para veiculos inativos ou em manutencao.");
                return;
            }

            estrutura.enfileirar(nova);
            processarFila();
            FormDialogs.info(this, "Movimentacao registrada.");
        }
    }

    private void edit() {
        Movement selected = selected();
        if (selected == null) return;
        FormDialogs.MovementForm form = FormDialogs.movement(
                this, data.vehicles(), data.expenseTypes(), selected);
        if (form != null) {
            data.updateMovement(selected, form.vehicle(), form.category(),
                    form.description(), form.date(), form.value(), form.mileage());
            FormDialogs.info(this, "Movimentacao atualizada.");
        }
    }

    private void delete() {
        Movement selected = selected();
        if (selected != null
                && FormDialogs.confirm(this, "Excluir a movimentacao selecionada?")) {
            data.removeMovement(selected);
            FormDialogs.info(this, "Movimentacao excluida.");
        }
    }

    private void processarFila() {
        while (!estrutura.estaVazia()) {
            // Retira o primeiro da fila (FIFO) e confirma no sistema
            Movement processada = estrutura.desenfileirar();
            System.out.println("Movimentacao processada da fila: "
                    + processada.description()
                    + " | Veiculo: " + processada.vehicle().plate()
                    + " | Valor: R$ " + processada.value());
        }
    }

    private Movement selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            FormDialogs.error(this, "Selecione uma movimentacao na tabela.");
            return null;
        }
        return visibleMovements.get(table.convertRowIndexToModel(row));
    }

    private void refresh() {
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        visibleMovements = data.movements();
        model.setRowCount(0);
        visibleMovements.forEach(m -> model.addRow(new Object[]{
                m.id(),
                m.vehicle().plate(),
                m.category().name(),
                m.description(),
                m.date().format(date),
                currency.format(m.value()),
                m.mileage() > 0 ? String.format("%.1f km", m.mileage()) : "-"
        }));
    }
}