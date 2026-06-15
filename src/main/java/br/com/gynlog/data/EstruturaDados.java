package br.com.gynlog.data;

import br.com.gynlog.model.Movement;
import br.com.gynlog.model.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EstruturaDados {

    private final ArrayList<Movement> fila = new ArrayList<>();

    public void enfileirar(Movement movimentacao) {
        fila.add(movimentacao); // adiciona sempre no final
    }

    public Movement desenfileirar() {
        if (fila.isEmpty()) return null;
        return fila.remove(0); // remove sempre o primeiro (FIFO)
    }

    public Movement espiar() {
        if (fila.isEmpty()) return null;
        return fila.get(0);
    }

    public boolean estaVazia() {
        return fila.isEmpty();
    }

    public int tamanho() {
        return fila.size();
    }

    public List<Movement> listarFila() {
        return new ArrayList<>(fila);
    }

    public List<Vehicle> ordenarVeiculosPorCusto(
            List<Vehicle> veiculos,
            java.util.Map<Integer, BigDecimal> totaisPorId,
            boolean crescente) {

        // Cria uma cópia para não modificar a lista original
        List<Vehicle> lista = new ArrayList<>(veiculos);
        int tamanho = lista.size();

        for (int i = 0; i < tamanho - 1; i++) {

            int indiceSelecionado = i;

            for (int j = i + 1; j < tamanho; j++) {
                BigDecimal custoJ = totaisPorId.getOrDefault(lista.get(j).id(), BigDecimal.ZERO);
                BigDecimal custoSelecionado = totaisPorId.getOrDefault(lista.get(indiceSelecionado).id(), BigDecimal.ZERO);

                if (crescente) {
                    if (custoJ.compareTo(custoSelecionado) < 0) {
                        indiceSelecionado = j;
                    }
                } else {
                    if (custoJ.compareTo(custoSelecionado) > 0) {
                        indiceSelecionado = j;
                    }
                }
            }

            if (indiceSelecionado != i) {
                Vehicle temp = lista.get(i);
                lista.set(i, lista.get(indiceSelecionado));
                lista.set(indiceSelecionado, temp);
            }
        }

        return lista;
    }

    public List<Vehicle> buscarVeiculos(List<Vehicle> veiculos, String termo) {
        List<Vehicle> resultado = new ArrayList<>();

        if (termo == null || termo.isBlank()) {
            // Se o termo estiver vazio, retorna todos os veículos
            return new ArrayList<>(veiculos);
        }

        String termoBusca = termo.toLowerCase().trim();

        // Percorre todos os veículos sequencialmente
        for (int i = 0; i < veiculos.size(); i++) {
            Vehicle veiculo = veiculos.get(i);

            boolean placaBate  = veiculo.plate().toLowerCase().contains(termoBusca);
            boolean modeloBate = veiculo.model().toLowerCase().contains(termoBusca);

            if (placaBate || modeloBate) {
                resultado.add(veiculo);
            }
        }

        return resultado;
    }
}