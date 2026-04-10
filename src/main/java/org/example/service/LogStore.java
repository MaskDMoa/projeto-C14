package org.example.service;

import org.example.model.LogEntry;
import org.example.model.LogType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Armazena todas as entradas de log de forma thread-safe.
 * Fornece filtragem por tipo e contagem.
 * 
 * Todos os métodos são synchronized para garantir segurança
 * quando a thread do FileWatcher adiciona entradas enquanto
 * a thread do JavaFX lê para exibição.
 */
public class LogStore {

    private final List<LogEntry> entradas = new ArrayList<>();

    /**
     * Adiciona novas entradas ao armazenamento.
     * Chamado pela thread do LogFileWatcher.
     */
    public synchronized void addEntradas(List<LogEntry> novas) {
        entradas.addAll(novas);
    }

    /**
     * Remove todas as entradas. Usado ao carregar um novo arquivo.
     */
    public synchronized void limpar() {
        entradas.clear();
    }

    /**
     * Retorna entradas filtradas por tipo.
     * ALL retorna uma cópia de todas as entradas.
     *
     * @param tipo o tipo de filtro desejado
     * @return lista filtrada (cópia defensiva)
     */
    public synchronized List<LogEntry> filtrar(LogType tipo) {
        if (tipo == LogType.ALL) {
            return new ArrayList<>(entradas);
        }
        return entradas.stream()
                .filter(e -> e.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    /**
     * Retorna o número total de entradas armazenadas.
     */
    public synchronized int tamanho() {
        return entradas.size();
    }
}
