package org.example.model;

/**
 * Representa uma única linha de log com seu texto e tipo classificado.
 * Imutável por design — uma vez criada, não pode ser alterada.
 */
public class LogEntry {

    private final String texto;
    private final LogType tipo;

    public LogEntry(String texto, LogType tipo) {
        this.texto = texto;
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }

    public LogType getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "[" + tipo.name() + "] " + texto;
    }
}
