package org.example.service;

import org.example.model.LogEntry;
import org.example.model.LogType;

/**
 * Responsável por analisar uma linha de texto e classificá-la
 * no tipo correto de log (ERROR, WARNING, INFO ou NORMAL).
 */
public class LogParser {

    /**
     * Analisa uma linha de texto e retorna um LogEntry com o tipo correto.
     *
     * @param linha texto raw da linha do arquivo
     * @return LogEntry com tipo classificado
     */
    public LogEntry parse(String linha) {
        String upper = linha.toUpperCase();
        LogType tipo;

        if (upper.contains("ERROR") || upper.contains("ERRO")) {
            tipo = LogType.ERROR;
        } else if (upper.contains("WARN") || upper.contains("WARNING")) {
            tipo = LogType.WARNING;
        } else if (upper.contains("INFO")) {
            tipo = LogType.INFO;
        } else {
            tipo = LogType.NORMAL;
        }

        return new LogEntry(linha, tipo);
    }
}
