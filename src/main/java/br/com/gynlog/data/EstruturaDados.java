package br.com.gynlog.data;

import br.com.gynlog.model.Movement;
import br.com.gynlog.model.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação manual das estruturas de dados exigidas pelo PI — Estrutura de Dados I.
 *
 * Contém três recursos:
 *   1. Fila (Queue) — controla movimentações na ordem em que foram cadastradas
 *   2. Ordenação manual (Selection Sort) — ordena veículos por custo total
 *   3. Busca sequencial — pesquisa veículos por placa ou modelo
 *
 * Nenhuma biblioteca pronta de ordenação ou busca foi utilizada (sem Collections.sort,
 * sem Stream.sorted, sem Arrays.sort). Tudo implementado manualmente, conforme exigido.
 */
public class EstruturaDados {

    // =========================================================================
    // 1. FILA DE MOVIMENTAÇÕES (Estrutura de Dados Linear — Fila / Queue)
    // =========================================================================
    //
    // Uma fila segue o princípio FIFO: First In, First Out.
    // O primeiro elemento que entra é o primeiro a sair.
    // Usamos isso para controlar a ordem de cadastro das movimentações,
    // permitindo processar despesas na sequência em que foram registradas.
    //
    // Operações:
    //   enfileirar() → adiciona no final da fila
    //   desenfileirar() → remove e retorna o primeiro da fila
    //   espiar() → lê o primeiro sem remover
    //   estaVazia() → verifica se a fila está vazia
    //   tamanho() → retorna quantos elementos estão na fila

    private final ArrayList<Movement> fila = new ArrayList<>();

    /** Adiciona uma movimentação no final da fila. */
    public void enfileirar(Movement movimentacao) {
        fila.add(movimentacao); // adiciona sempre no final
    }

    /** Remove e retorna a primeira movimentação da fila. Retorna null se vazia. */
    public Movement desenfileirar() {
        if (fila.isEmpty()) return null;
        return fila.remove(0); // remove sempre o primeiro (FIFO)
    }

    /** Lê a primeira movimentação sem removê-la. Retorna null se vazia. */
    public Movement espiar() {
        if (fila.isEmpty()) return null;
        return fila.get(0);
    }

    /** Retorna true se a fila não tiver nenhuma movimentação. */
    public boolean estaVazia() {
        return fila.isEmpty();
    }

    /** Retorna quantas movimentações estão na fila. */
    public int tamanho() {
        return fila.size();
    }

    /** Retorna uma cópia da fila atual para exibição (sem modificar a original). */
    public List<Movement> listarFila() {
        return new ArrayList<>(fila);
    }

    // =========================================================================
    // 2. ORDENAÇÃO MANUAL — Selection Sort (Algoritmo de Ordenação)
    // =========================================================================
    //
    // O Selection Sort funciona assim:
    //   - Percorre a lista procurando o menor (ou maior) elemento
    //   - Troca esse elemento com o da posição atual
    //   - Repete para a próxima posição até ordenar tudo
    //
    // Aqui ordenamos veículos pelo custo total de despesas.
    // Isso serve para o relatório 10 (maior e menor custo) e é
    // exigido pelo PI como algoritmo implementado manualmente.
    //
    // Parâmetros:
    //   veiculos       → lista de veículos a ordenar
    //   totaisPorId    → custo total de cada veículo (indexado pelo id do veículo)
    //   crescente      → true = menor para maior, false = maior para menor

    public List<Vehicle> ordenarVeiculosPorCusto(
            List<Vehicle> veiculos,
            java.util.Map<Integer, BigDecimal> totaisPorId,
            boolean crescente) {

        // Cria uma cópia para não modificar a lista original
        List<Vehicle> lista = new ArrayList<>(veiculos);
        int tamanho = lista.size();

        // Selection Sort — O(n²), simples e fácil de explicar
        for (int i = 0; i < tamanho - 1; i++) {

            // Assume que o elemento da posição i é o "candidato" atual
            int indiceSelecionado = i;

            for (int j = i + 1; j < tamanho; j++) {
                BigDecimal custoJ = totaisPorId.getOrDefault(lista.get(j).id(), BigDecimal.ZERO);
                BigDecimal custoSelecionado = totaisPorId.getOrDefault(lista.get(indiceSelecionado).id(), BigDecimal.ZERO);

                // Compara e atualiza o índice do candidato conforme a direção
                if (crescente) {
                    if (custoJ.compareTo(custoSelecionado) < 0) {
                        indiceSelecionado = j; // encontrou um menor
                    }
                } else {
                    if (custoJ.compareTo(custoSelecionado) > 0) {
                        indiceSelecionado = j; // encontrou um maior
                    }
                }
            }

            // Troca o elemento da posição i com o candidato encontrado
            if (indiceSelecionado != i) {
                Vehicle temp = lista.get(i);
                lista.set(i, lista.get(indiceSelecionado));
                lista.set(indiceSelecionado, temp);
            }
        }

        return lista;
    }

    // =========================================================================
    // 3. BUSCA SEQUENCIAL (Pesquisa por placa ou modelo)
    // =========================================================================
    //
    // A busca sequencial percorre todos os elementos da lista um por um,
    // comparando cada um com o termo pesquisado.
    // É a forma mais simples de busca — O(n) — e funciona em qualquer lista,
    // ordenada ou não.
    //
    // Aqui buscamos veículos cuja placa ou modelo contenham o termo digitado.
    // A busca ignora maiúsculas/minúsculas para facilitar o uso.

    public List<Vehicle> buscarVeiculos(List<Vehicle> veiculos, String termo) {
        List<Vehicle> resultado = new ArrayList<>();

        if (termo == null || termo.isBlank()) {
            // Se o termo estiver vazio, retorna todos os veículos
            return new ArrayList<>(veiculos);
        }

        // Converte o termo para minúsculas para comparação sem distinção de case
        String termoBusca = termo.toLowerCase().trim();

        // Percorre todos os veículos sequencialmente — busca sequencial
        for (int i = 0; i < veiculos.size(); i++) {
            Vehicle veiculo = veiculos.get(i);

            boolean placaBate  = veiculo.plate().toLowerCase().contains(termoBusca);
            boolean modeloBate = veiculo.model().toLowerCase().contains(termoBusca);

            // Adiciona ao resultado se placa ou modelo contiverem o termo
            if (placaBate || modeloBate) {
                resultado.add(veiculo);
            }
        }

        return resultado;
    }
}