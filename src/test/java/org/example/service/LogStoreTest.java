package org.example.service;

import org.example.model.LogEntry;
import org.example.model.LogType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogStoreTest {

    private LogStore store;

    @BeforeEach
    void setUp() {
        store = new LogStore();
    }

    // --- Fluxo Normal (6 cenários) ---

    @Test
    @DisplayName("Deve adicionar uma entrada corretamente")
    void testAddSingleEntry() {
        LogEntry entry = new LogEntry("Log 1", LogType.INFO);
        store.addEntradas(Arrays.asList(entry));
        assertEquals(1, store.tamanho());
        assertEquals(entry, store.filtrar(LogType.ALL).get(0));
    }

    @Test
    @DisplayName("Deve adicionar múltiplas entradas corretamente")
    void testAddMultipleEntries() {
        List<LogEntry> entries = Arrays.asList(
            new LogEntry("Log 1", LogType.INFO),
            new LogEntry("Log 2", LogType.ERROR)
        );
        store.addEntradas(entries);
        assertEquals(2, store.tamanho());
    }

    @Test
    @DisplayName("Deve filtrar apenas entradas do tipo ERROR")
    void testFilterError() {
        store.addEntradas(Arrays.asList(
            new LogEntry("Erro 1", LogType.ERROR),
            new LogEntry("Info 1", LogType.INFO),
            new LogEntry("Erro 2", LogType.ERROR)
        ));
        List<LogEntry> filtered = store.filtrar(LogType.ERROR);
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(e -> e.getTipo() == LogType.ERROR));
    }

    @Test
    @DisplayName("Deve filtrar apenas entradas do tipo INFO")
    void testFilterInfo() {
        store.addEntradas(Arrays.asList(
            new LogEntry("Erro 1", LogType.ERROR),
            new LogEntry("Info 1", LogType.INFO)
        ));
        List<LogEntry> filtered = store.filtrar(LogType.INFO);
        assertEquals(1, filtered.size());
        assertEquals(LogType.INFO, filtered.get(0).getTipo());
    }

    @Test
    @DisplayName("Deve retornar todas as entradas ao filtrar por ALL")
    void testFilterAll() {
        store.addEntradas(Arrays.asList(
            new LogEntry("Erro 1", LogType.ERROR),
            new LogEntry("Info 1", LogType.INFO)
        ));
        List<LogEntry> all = store.filtrar(LogType.ALL);
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Deve limpar todas as entradas corretamente")
    void testLimpar() {
        store.addEntradas(Arrays.asList(new LogEntry("X", LogType.INFO)));
        store.limpar();
        assertEquals(0, store.tamanho());
        assertTrue(store.filtrar(LogType.ALL).isEmpty());
    }

    // --- Fluxo de Extensão (4 cenários) ---

    @Test
    @DisplayName("Deve retornar lista vazia ao filtrar tipo inexistente no store")
    void testFilterNonExistentType() {
        store.addEntradas(Arrays.asList(new LogEntry("Info 1", LogType.INFO)));
        List<LogEntry> filtered = store.filtrar(LogType.ERROR);
        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    @DisplayName("Deve lidar com filtragem em store vazio")
    void testFilterEmptyStore() {
        List<LogEntry> filtered = store.filtrar(LogType.ALL);
        assertTrue(filtered.isEmpty());
    }

    @Test
    @DisplayName("Não deve alterar o tamanho ao adicionar uma lista vazia")
    void testAddEmptyList() {
        store.addEntradas(new ArrayList<>());
        assertEquals(0, store.tamanho());
    }

    @Test
    @DisplayName("Deve garantir que a lista retornada por filtrar é uma cópia (Thread-Safety)")
    void testDefensiveCopy() {
        LogEntry entry = new LogEntry("Log 1", LogType.INFO);
        store.addEntradas(Arrays.asList(entry));
        
        List<LogEntry> filtered = store.filtrar(LogType.ALL);
        assertEquals(1, filtered.size());
        
        // Se eu limpar o store, a lista recuperada anteriormente não deve ser afetada
        store.limpar();
        assertEquals(1, filtered.size()); 
        assertEquals(0, store.tamanho());
    }
}
