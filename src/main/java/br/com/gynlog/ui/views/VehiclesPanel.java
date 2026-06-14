package br.com.gynlog.ui.views;

import br.com.gynlog.data.AppData;
import br.com.gynlog.model.Vehicle;
import br.com.gynlog.ui.Theme;
import br.com.gynlog.ui.components.AppIcon;
import br.com.gynlog.ui.components.FormDialogs;
import br.com.gynlog.ui.components.UiFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public final class VehiclesPanel extends JPanel {
    private final AppData data;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Placa", "Modelo", "Marca", "Ano", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField search = UiFactory.field();
    private List<Vehicle> visibleVehicles = List.of();

    public VehiclesPanel(AppData data) {
        this.data = data;
        setLayout(new BorderLayout());
        JButton add = UiFactory.button("Novo", AppIcon.Type.ADD, Theme.SUCCESS);
        JButton edit = UiFactory.button("Editar", AppIcon.Type.EDIT, Theme.PRIMARY);
        JButton delete = UiFactory.button("Excluir", AppIcon.Type.DELETE, Theme.DANGER);
        JButton find = UiFactory.button("Buscar", AppIcon.Type.SEARCH, Theme.NAVY_LIGHT);
        add.addActionListener(event -> add());
        edit.addActionListener(event -> edit());
        delete.addActionListener(event -> delete());
        find.addActionListener(event -> refresh());
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { refresh(); }
            public void removeUpdate(DocumentEvent event) { refresh(); }
            public void changedUpdate(DocumentEvent event) { refresh(); }
        });

        JPanel card = UiFactory.card();
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);
        top.add(UiFactory.toolbar(add, edit, delete), BorderLayout.WEST);
        JPanel searchBar = new JPanel(new BorderLayout(6, 0));
        searchBar.setOpaque(false);
        searchBar.add(search, BorderLayout.CENTER);
        searchBar.add(find, BorderLayout.EAST);
        top.add(searchBar, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);
        card.add(UiFactory.tableScroll(table), BorderLayout.CENTER);
        add(UiFactory.page("Veiculos", "Cadastro e consulta da frota", card), BorderLayout.CENTER);
        data.addListener(this::refresh);
        refresh();
    }

    private void add() {
        FormDialogs.VehicleForm form = FormDialogs.vehicle(this, null);
        if (form != null) {
            Vehicle novo = data.addVehicle(form.plate(), form.model(), form.brand(), form.year(), form.status());
            if (novo == null) {
                FormDialogs.error(this, "Ja existe um veiculo cadastrado com essa placa.");
            } else {
                FormDialogs.info(this, "Veiculo cadastrado com sucesso.");
            }
        }
    }

    private void edit() {
        Vehicle selected = selected();
        if (selected == null) return;
        FormDialogs.VehicleForm form = FormDialogs.vehicle(this, selected);
        if (form != null) {
            data.updateVehicle(selected, form.plate(), form.model(), form.brand(), form.year(), form.status());
            FormDialogs.info(this, "Veiculo atualizado com sucesso.");
        }
    }

    private void delete() {
        Vehicle selected = selected();
        if (selected == null || !FormDialogs.confirm(this, "Excluir o veiculo " + selected.plate() + "?")) return;
        if (data.removeVehicle(selected)) {
            FormDialogs.info(this, "Veiculo excluido.");
        } else {
            FormDialogs.error(this, "O veiculo possui movimentacoes e nao pode ser excluido.");
        }
    }

    private Vehicle selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            FormDialogs.error(this, "Selecione um veiculo na tabela.");
            return null;
        }
        return visibleVehicles.get(table.convertRowIndexToModel(row));
    }

    private void refresh() {
        String term = search.getText().trim().toLowerCase();
        visibleVehicles = data.vehicles().stream()
                .filter(vehicle -> term.isBlank() || (vehicle.plate() + vehicle.model() + vehicle.brand() + vehicle.status())
                        .toLowerCase().contains(term))
                .toList();
        model.setRowCount(0);
        visibleVehicles.forEach(vehicle -> model.addRow(new Object[]{
                vehicle.id(), vehicle.plate(), vehicle.model(), vehicle.brand(), vehicle.year(), vehicle.status()}));
    }
}
