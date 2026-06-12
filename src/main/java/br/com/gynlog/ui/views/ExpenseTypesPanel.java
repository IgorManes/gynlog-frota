package br.com.gynlog.ui.views;

import br.com.gynlog.data.AppData;
import br.com.gynlog.model.ExpenseType;
import br.com.gynlog.ui.Theme;
import br.com.gynlog.ui.components.AppIcon;
import br.com.gynlog.ui.components.FormDialogs;
import br.com.gynlog.ui.components.UiFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public final class ExpenseTypesPanel extends JPanel {
    private final AppData data;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nome", "Descricao"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<ExpenseType> visibleTypes = List.of();

    public ExpenseTypesPanel(AppData data) {
        this.data = data;
        setLayout(new BorderLayout());
        JButton add = UiFactory.button("Novo", AppIcon.Type.ADD, Theme.SUCCESS);
        JButton edit = UiFactory.button("Editar", AppIcon.Type.EDIT, Theme.PRIMARY);
        JButton delete = UiFactory.button("Excluir", AppIcon.Type.DELETE, Theme.DANGER);
        add.addActionListener(event -> add());
        edit.addActionListener(event -> edit());
        delete.addActionListener(event -> delete());

        JPanel card = UiFactory.card();
        card.add(UiFactory.toolbar(add, edit, delete), BorderLayout.NORTH);
        card.add(UiFactory.tableScroll(table), BorderLayout.CENTER);
        add(UiFactory.page("Tipos de Despesa", "Categorias utilizadas nas movimentacoes", card), BorderLayout.CENTER);
        data.addListener(this::refresh);
        refresh();
    }

    private void add() {
        FormDialogs.ExpenseTypeForm form = FormDialogs.expenseType(this, null);
        if (form != null) {
            data.addExpenseType(form.name(), form.description());
            FormDialogs.info(this, "Tipo de despesa cadastrado.");
        }
    }

    private void edit() {
        ExpenseType selected = selected();
        if (selected == null) return;
        FormDialogs.ExpenseTypeForm form = FormDialogs.expenseType(this, selected);
        if (form != null) {
            data.updateExpenseType(selected, form.name(), form.description());
            FormDialogs.info(this, "Tipo de despesa atualizado.");
        }
    }

    private void delete() {
        ExpenseType selected = selected();
        if (selected == null || !FormDialogs.confirm(this, "Excluir o tipo " + selected.name() + "?")) return;
        if (data.removeExpenseType(selected)) {
            FormDialogs.info(this, "Tipo de despesa excluido.");
        } else {
            FormDialogs.error(this, "A categoria possui movimentacoes e nao pode ser excluida.");
        }
    }

    private ExpenseType selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            FormDialogs.error(this, "Selecione um tipo de despesa na tabela.");
            return null;
        }
        return visibleTypes.get(table.convertRowIndexToModel(row));
    }

    private void refresh() {
        visibleTypes = data.expenseTypes();
        model.setRowCount(0);
        visibleTypes.forEach(type -> model.addRow(new Object[]{type.id(), type.name(), type.description()}));
    }
}
