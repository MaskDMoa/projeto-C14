package org.example.service;

import org.example.model.LogEntry;

import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Thread daemon que monitora um arquivo de log em tempo real.
 * 
 * Usa RandomAccessFile para rastrear a posição do último byte lido,
 * garantindo que linhas já processadas não sejam relidas.
 * 
 * Quando novas linhas são detectadas, elas são parseadas pelo LogParser
 * e enviadas ao callback (que normalmente atualiza a UI via Platform.runLater).
 * 
 * Se o arquivo for truncado (tamanho menor que a posição anterior),
 * a leitura recomeça do início automaticamente.
 */
public class LogFileWatcher extends Thread {

    private final Path caminhoArquivo;
    private final LogParser parser;
    private final Consumer<List<LogEntry>> onNovasEntradas;
    private volatile boolean rodando = true;
    private long ultimaPosicao = 0;

    /** Intervalo entre verificações (em milissegundos) */
    private static final long INTERVALO_MS = 1000;

    /**
     * Cria um novo watcher para o arquivo especificado.
     *
     * @param caminhoArquivo caminho do arquivo a monitorar
     * @param onNovasEntradas callback chamado quando novas linhas são encontradas
     */
    public LogFileWatcher(Path caminhoArquivo, Consumer<List<LogEntry>> onNovasEntradas) {
        this.caminhoArquivo = caminhoArquivo;
        this.parser = new LogParser();
        this.onNovasEntradas = onNovasEntradas;
        setDaemon(true);
        setName("LogFileWatcher-" + caminhoArquivo.getFileName());
    }

    @Override
    public void run() {
        while (rodando) {
            try {
                List<LogEntry> novas = lerNovasLinhas();
                if (!novas.isEmpty()) {
                    onNovasEntradas.accept(novas);
                }
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[LogFileWatcher] Erro ao monitorar: " + e.getMessage());
            }
        }
    }

    /**
     * Lê apenas as linhas novas desde a última verificação.
     * Usa RandomAccessFile com seek para eficiência — não relê o arquivo inteiro.
     */
    private List<LogEntry> lerNovasLinhas() throws Exception {
        List<LogEntry> novas = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivo.toFile(), "r")) {
            long tamanhoArquivo = raf.length();

            // Se o arquivo foi truncado/resetado, recomeça do início
            if (tamanhoArquivo < ultimaPosicao) {
                ultimaPosicao = 0;
            }

            // Sem dados novos
            if (tamanhoArquivo == ultimaPosicao) {
                return novas;
            }

            // Pula para onde parou na última leitura
            raf.seek(ultimaPosicao);

            String linha;
            while ((linha = raf.readLine()) != null) {
                // readLine() retorna ISO-8859-1, converte para UTF-8
                linha = new String(linha.getBytes("ISO-8859-1"), "UTF-8");
                if (!linha.isEmpty()) {
                    novas.add(parser.parse(linha));
                }
            }

            // Salva posição para a próxima verificação
            ultimaPosicao = raf.getFilePointer();
        }

        return novas;
    }

    /**
     * Para o monitoramento de forma graciosa.
     */
    public void parar() {
        rodando = false;
        interrupt();
    }
}
