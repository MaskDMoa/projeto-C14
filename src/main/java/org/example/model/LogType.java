package org.example.model;

/**
 * Tipos possíveis de uma entrada de log.
 * ALL é usado apenas para filtragem (mostra todos os tipos).
 */
public enum LogType {
    ALL,      // Filtro especial: mostra tudo
    ERROR,    // Linhas com [Erro] ou [Error]
    WARNING,  // Linhas com [Warning] ou [Warn]
    INFO,     // Linhas com [Info]
    NORMAL    // Linhas sem classificação específica
}
