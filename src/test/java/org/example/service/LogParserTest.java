package org.example.service;

import org.example.model.LogEntry;
import org.example.model.LogType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class LogParserTest {

    private LogParser parser;

    @BeforeEach
    void setUp() {
        parser = new LogParser();
    }

    // --- Fluxo Normal (4 cenários) ---

    @Test
    @DisplayName("Deve identificar um log do tipo ERROR")
    void testParseError() {
        LogEntry entry = parser.parse("ERROR: Conexão falhou");
        assertEquals(LogType.ERROR, entry.getTipo());
    }

    @Test
    @DisplayName("Deve identificar um log do tipo WARNING")
    void testParseWarning() {
        LogEntry entry = parser.parse("WARNING: Memória alta");
        assertEquals(LogType.WARNING, entry.getTipo());
    }

    @Test
    @DisplayName("Deve identificar um log do tipo INFO")
    void testParseInfo() {
        LogEntry entry = parser.parse("INFO: Sistema iniciado");
        assertEquals(LogType.INFO, entry.getTipo());
    }

    @Test
    @DisplayName("Deve identificar como NORMAL uma linha sem palavras-chave")
    void testParseNormal() {
        LogEntry entry = parser.parse("Apenas um texto qualquer");
        assertEquals(LogType.NORMAL, entry.getTipo());
    }

    // --- Fluxo de Extensão (6 cenários) ---

    @ParameterizedTest
    @ValueSource(strings = {"error: mensagem minúscula", "ErRoR: mensagem mista"})
    @DisplayName("Deve ser case-insensitive para ERROR")
    void testParseErrorCaseInsensitive(String linha) {
        LogEntry entry = parser.parse(linha);
        assertEquals(LogType.ERROR, entry.getTipo());
    }

    @Test
    @DisplayName("Deve identificar 'ERRO' como ERROR (alternativa em PT)")
    void testParseErroAlternative() {
        LogEntry entry = parser.parse("[ERRO] Falha crítica");
        assertEquals(LogType.ERROR, entry.getTipo());
    }

    @Test
    @DisplayName("Deve identificar 'WARN' como WARNING (abreviação)")
    void testParseWarnShorthand() {
        LogEntry entry = parser.parse("WARN - Espaço em disco baixo");
        assertEquals(LogType.WARNING, entry.getTipo());
    }

    @Test
    @DisplayName("Deve lidar com linha vazia como NORMAL")
    void testParseEmptyLine() {
        LogEntry entry = parser.parse("");
        assertEquals(LogType.NORMAL, entry.getTipo());
    }

    @Test
    @DisplayName("Deve identificar palavra-chave mesmo grudada em outros caracteres")
    void testParseKeywordInsideText() {
        LogEntry entry = parser.parse("CriticalERROROccured!");
        assertEquals(LogType.ERROR, entry.getTipo());
    }

    @Test
    @DisplayName("Deve priorizar ERROR sobre INFO se ambos existirem")
    void testParsePriority() {
        // Pela lógica atual de if/else no LogParser, ERROR vem primeiro
        LogEntry entry = parser.parse("INFO: Ocorreu um ERROR crítico");
        assertEquals(LogType.ERROR, entry.getTipo());
    }
}
