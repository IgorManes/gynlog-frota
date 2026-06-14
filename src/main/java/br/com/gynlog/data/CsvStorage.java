package br.com.gynlog.data;

import br.com.gynlog.model.ExpenseType;
import br.com.gynlog.model.Movement;
import br.com.gynlog.model.Vehicle;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por ler e gravar os dados em arquivos CSV.
 * Cada entidade tem seu próprio arquivo na pasta "dados".
 *
 * Estrutura dos arquivos:
 *   veiculos.csv        → id;placa;modelo;marca;ano;status
 *   tipos_despesa.csv   → id;nome;descricao
 *   movimentacoes.csv   → id;idVeiculo;idTipoDespesa;descricao;data;valor;quilometragem
 */
public class CsvStorage {

    private static final Path PASTA              = Paths.get("dados");
    private static final Path ARQUIVO_VEICULOS   = PASTA.resolve("veiculos.csv");
    private static final Path ARQUIVO_TIPOS      = PASTA.resolve("tipos_despesa.csv");
    private static final Path ARQUIVO_MOVS       = PASTA.resolve("movimentacoes.csv");
    private static final String SEP              = ";";

    /** Garante que a pasta "dados" existe ao iniciar o sistema. */
    public static void inicializar() {
        try {
            Files.createDirectories(PASTA);
        } catch (IOException e) {
            throw new RuntimeException("Nao foi possivel criar a pasta de dados.", e);
        }
    }

    // ─── VEÍCULOS ────────────────────────────────────────────────────────────

    public static List<Vehicle> lerVeiculos() {
        List<Vehicle> lista = new ArrayList<>();
        if (!Files.exists(ARQUIVO_VEICULOS)) return lista;

        for (String linha : lerLinhas(ARQUIVO_VEICULOS)) {
            String[] c = linha.split(SEP, -1);
            if (c.length < 6) continue;
            lista.add(new Vehicle(
                    Integer.parseInt(c[0]), // id
                    c[1],                   // placa
                    c[2],                   // modelo
                    c[3],                   // marca
                    Integer.parseInt(c[4]), // ano
                    c[5]                    // status
            ));
        }
        return lista;
    }

    public static void gravarVeiculos(List<Vehicle> veiculos) {
        List<String> linhas = new ArrayList<>();
        for (Vehicle v : veiculos) {
            linhas.add(v.id() + SEP + v.plate() + SEP + v.model() + SEP
                    + v.brand() + SEP + v.year() + SEP + v.status());
        }
        gravarLinhas(ARQUIVO_VEICULOS, linhas);
    }

    // ─── TIPOS DE DESPESA ────────────────────────────────────────────────────

    public static List<ExpenseType> lerTiposDespesa() {
        List<ExpenseType> lista = new ArrayList<>();
        if (!Files.exists(ARQUIVO_TIPOS)) return lista;

        for (String linha : lerLinhas(ARQUIVO_TIPOS)) {
            String[] c = linha.split(SEP, -1);
            if (c.length < 3) continue;
            lista.add(new ExpenseType(
                    Integer.parseInt(c[0]), // id
                    c[1],                   // nome
                    c[2]                    // descricao
            ));
        }
        return lista;
    }

    public static void gravarTiposDespesa(List<ExpenseType> tipos) {
        List<String> linhas = new ArrayList<>();
        for (ExpenseType t : tipos) {
            linhas.add(t.id() + SEP + t.name() + SEP + t.description());
        }
        gravarLinhas(ARQUIVO_TIPOS, linhas);
    }

    // ─── MOVIMENTAÇÕES ───────────────────────────────────────────────────────

    public static List<Movement> lerMovimentacoes(List<Vehicle> veiculos, List<ExpenseType> tipos) {
        List<Movement> lista = new ArrayList<>();
        if (!Files.exists(ARQUIVO_MOVS)) return lista;

        for (String linha : lerLinhas(ARQUIVO_MOVS)) {
            String[] c = linha.split(SEP, -1);
            if (c.length < 6) continue;

            Vehicle    veiculo = buscarVeiculoPorId(veiculos, Integer.parseInt(c[1]));
            ExpenseType tipo   = buscarTipoPorId(tipos, Integer.parseInt(c[2]));
            if (veiculo == null || tipo == null) continue;

            // Quilometragem é opcional — arquivos antigos podem não ter o campo
            double km = c.length > 6 ? Double.parseDouble(c[6]) : 0.0;

            lista.add(new Movement(
                    Integer.parseInt(c[0]), // id
                    veiculo,
                    tipo,
                    c[3],                   // descricao
                    LocalDate.parse(c[4]),  // data
                    new BigDecimal(c[5]),   // valor
                    km                      // quilometragem
            ));
        }
        return lista;
    }

    public static void gravarMovimentacoes(List<Movement> movs) {
        List<String> linhas = new ArrayList<>();
        for (Movement m : movs) {
            linhas.add(m.id() + SEP + m.vehicle().id() + SEP + m.category().id()
                    + SEP + m.description() + SEP + m.date()
                    + SEP + m.value() + SEP + m.mileage());
        }
        gravarLinhas(ARQUIVO_MOVS, linhas);
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private static List<String> lerLinhas(Path arquivo) {
        try {
            return Files.readAllLines(arquivo, StandardCharsets.UTF_8)
                    .stream().filter(l -> !l.isBlank()).toList();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler: " + arquivo, e);
        }
    }

    private static void gravarLinhas(Path arquivo, List<String> linhas) {
        try {
            Files.write(arquivo, linhas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gravar: " + arquivo, e);
        }
    }

    private static Vehicle buscarVeiculoPorId(List<Vehicle> veiculos, int id) {
        for (Vehicle v : veiculos) if (v.id() == id) return v;
        return null;
    }

    private static ExpenseType buscarTipoPorId(List<ExpenseType> tipos, int id) {
        for (ExpenseType t : tipos) if (t.id() == id) return t;
        return null;
    }
}