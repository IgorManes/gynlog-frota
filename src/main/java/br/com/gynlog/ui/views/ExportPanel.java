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
        JPanel opcoes = new JPanel(new GridLayout(1, 3, 16, 16));
        opcoes.setOpaque(false);
        opcoes.add(opcao("Veiculos",        "Exporta o cadastro completo da frota.",   this::exportarVeiculos));
        opcoes.add(opcao("Tipos de despesa","Exporta as categorias cadastradas.",       this::exportarTipos));
        opcoes.add(opcao("Movimentacoes",   "Exporta o historico financeiro completo.", this::exportarMovimentacoes));
        add(UiFactory.page("Exportacao", "Gere arquivos CSV compativeis com planilhas eletronicas", opcoes),
                BorderLayout.CENTER);
    }

    private JPanel opcao(String titulo, String descricao, Runnable acao) {
        JPanel card = UiFactory.card();
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel lblDescricao = new JLabel("<html>" + descricao + "</html>");
        lblDescricao.setForeground(Theme.MUTED);
        JButton exportar = UiFactory.button("Exportar CSV", AppIcon.Type.EXPORT, Theme.PRIMARY);
        exportar.addActionListener(e -> acao.run());
        card.add(lblTitulo,   BorderLayout.NORTH);
        card.add(lblDescricao, BorderLayout.CENTER);
        card.add(UiFactory.toolbar(exportar), BorderLayout.SOUTH);
        return card;
    }

    private void exportarVeiculos() {
        List<String> linhas = new ArrayList<>();
        // Cabeçalho do CSV — compatível com Excel e LibreOffice
        linhas.add("id;placa;modelo;marca;ano;status");
        data.vehicles().forEach(v -> linhas.add(String.join(";",
                String.valueOf(v.id()), v.plate(), v.model(),
                v.brand(), String.valueOf(v.year()), v.status())));
        salvar("veiculos.csv", linhas);
    }

    private void exportarTipos() {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;nome;descricao");
        data.expenseTypes().forEach(t -> linhas.add(String.join(";",
                String.valueOf(t.id()), t.name(), t.description())));
        salvar("tipos_despesa.csv", linhas);
    }

    private void exportarMovimentacoes() {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;veiculo;categoria;descricao;data;valor;quilometragem");
        for (Movement m : data.movements()) {
            linhas.add(String.join(";",
                    String.valueOf(m.id()),
                    m.vehicle().plate(),
                    m.category().name(),
                    m.description(),
                    m.date().toString(),
                    m.value().toPlainString(),
                    String.valueOf(m.mileage())
            ));
        }
        salvar("movimentacoes.csv", linhas);
    }

    private void salvar(String nomeArquivo, List<String> linhas) {
        JFileChooser seletor = new JFileChooser();
        seletor.setSelectedFile(new java.io.File(nomeArquivo));
        if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path caminho = seletor.getSelectedFile().toPath();
        try {
            Files.write(caminho, linhas, StandardCharsets.UTF_8);
            FormDialogs.info(this, "Arquivo exportado com sucesso:\n" + caminho);
        } catch (IOException e) {
            FormDialogs.error(this, "Nao foi possivel exportar:\n" + e.getMessage());
        }
    }
}