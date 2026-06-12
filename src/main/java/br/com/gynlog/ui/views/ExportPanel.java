package br.com.gynlog.ui.views;

import br.com.gynlog.data.AppData;
import br.com.gynlog.model.Movement;
import br.com.gynlog.ui.Theme;
import br.com.gynlog.ui.components.AppIcon;
import br.com.gynlog.ui.components.FormDialogs;
import br.com.gynlog.ui.components.UiFactory;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ExportPanel extends JPanel {
    private final AppData data;

    public ExportPanel(AppData data) {
        this.data = data;
        setLayout(new BorderLayout());
        JPanel options = new JPanel(new GridLayout(1, 3, 16, 16));
        options.setOpaque(false);
        options.add(option("Veiculos", "Exporta o cadastro completo da frota.", this::exportVehicles));
        options.add(option("Tipos de despesa", "Exporta as categorias cadastradas.", this::exportTypes));
        options.add(option("Movimentacoes", "Exporta o historico financeiro.", this::exportMovements));
        add(UiFactory.page("Exportacao", "Gere arquivos CSV compativeis com planilhas", options), BorderLayout.CENTER);
    }

    private JPanel option(String title, String description, Runnable action) {
        JPanel card = UiFactory.card();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel descriptionLabel = new JLabel("<html>" + description + "</html>");
        descriptionLabel.setForeground(Theme.MUTED);
        JButton export = UiFactory.button("Exportar CSV", AppIcon.Type.EXPORT, Theme.PRIMARY);
        export.addActionListener(event -> action.run());
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descriptionLabel, BorderLayout.CENTER);
        card.add(UiFactory.toolbar(export), BorderLayout.SOUTH);
        return card;
    }

    private void exportVehicles() {
        List<String> lines = new ArrayList<>();
        lines.add("id;placa;modelo;marca;ano;status");
        data.vehicles().forEach(vehicle -> lines.add(String.join(";",
                String.valueOf(vehicle.id()), vehicle.plate(), vehicle.model(), vehicle.brand(),
                String.valueOf(vehicle.year()), vehicle.status())));
        save("veiculos.csv", lines);
    }

    private void exportTypes() {
        List<String> lines = new ArrayList<>();
        lines.add("id;nome;descricao");
        data.expenseTypes().forEach(type ->
                lines.add(String.join(";", String.valueOf(type.id()), type.name(), type.description())));
        save("tipos-despesa.csv", lines);
    }

    private void exportMovements() {
        List<String> lines = new ArrayList<>();
        lines.add("id;veiculo;categoria;descricao;data;valor");
        for (Movement movement : data.movements()) {
            lines.add(String.join(";", String.valueOf(movement.id()), movement.vehicle().plate(),
                    movement.category().name(), movement.description(), movement.date().toString(),
                    movement.value().toPlainString()));
        }
        save("movimentacoes.csv", lines);
    }

    private void save(String suggestedName, List<String> lines) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(suggestedName));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
            FormDialogs.info(this, "Arquivo exportado com sucesso:\n" + path);
        } catch (IOException exception) {
            FormDialogs.error(this, "Nao foi possivel exportar o arquivo:\n" + exception.getMessage());
        }
    }
}
