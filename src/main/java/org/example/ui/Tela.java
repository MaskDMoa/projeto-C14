package org.example.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.example.model.LogEntry;
import org.example.model.LogType;
import org.example.service.LogFileWatcher;
import org.example.service.LogStore;

import java.io.File;
import java.util.List;

/**
 * Classe principal da interface gráfica.
 * Responsável APENAS pela apresentação visual.
 * 
 * Toda a lógica de dados está delegada para:
 * - LogStore (armazenamento e filtragem)
 * - LogFileWatcher (monitoramento em tempo real)
 * - LogParser (classificação de linhas)
 */
public class Tela extends Application {

    // --- Serviços ---
    private final LogStore logStore = new LogStore();
    private LogFileWatcher watcher;

    // --- Componentes UI ---
    private TextFlow textFlow;
    private ScrollPane scrollPane;
    private Label statusLabel;
    private Label contadorLabel;
    private Label monitorLabel;
    private Button btnAll, btnError, btnWarning, btnInfo;
    private Button botaoSelecionado;

    // --- Estado ---
    private LogType filtroAtual = LogType.ALL;

    @Override
    public void start(Stage stage) {

        // ═══════════════════════════════════════
        //  BARRA SUPERIOR
        // ═══════════════════════════════════════
        Button btnCarregar = new Button("📂 Carregar Log");
        btnCarregar.getStyleClass().add("btn-carregar");

        statusLabel = new Label("Nenhum arquivo carregado");
        statusLabel.getStyleClass().add("status-label");

        // Espaçador flexível para empurrar o contador para a direita
        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        contadorLabel = new Label("");
        contadorLabel.getStyleClass().add("contador-label");

        monitorLabel = new Label("");
        monitorLabel.getStyleClass().add("monitor-label");

        HBox barraSuperior = new HBox(10, btnCarregar, statusLabel, espacador, monitorLabel, contadorLabel);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.setPadding(new Insets(10));
        barraSuperior.getStyleClass().add("barra-superior");

        // ═══════════════════════════════════════
        //  BARRA DE FILTROS
        // ═══════════════════════════════════════
        btnAll     = criarBotaoFiltro("📋 All",     LogType.ALL);
        btnError   = criarBotaoFiltro("❌ Error",   LogType.ERROR);
        btnWarning = criarBotaoFiltro("⚠ Warning",  LogType.WARNING);
        btnInfo    = criarBotaoFiltro("ℹ Info",      LogType.INFO);

        btnAll.getStyleClass().add("filtro-all");
        btnError.getStyleClass().add("filtro-error");
        btnWarning.getStyleClass().add("filtro-warning");
        btnInfo.getStyleClass().add("filtro-info");

        HBox barraFiltros = new HBox(8, btnAll, btnError, btnWarning, btnInfo);
        barraFiltros.setAlignment(Pos.CENTER);
        barraFiltros.setPadding(new Insets(8, 10, 8, 10));
        barraFiltros.getStyleClass().add("barra-filtros");

        selecionarBotao(btnAll);

        // ═══════════════════════════════════════
        //  ÁREA DE LOG (TextFlow + ScrollPane)
        // ═══════════════════════════════════════
        textFlow = new TextFlow();
        textFlow.getStyleClass().add("text-flow-log");
        textFlow.setPadding(new Insets(12));

        scrollPane = new ScrollPane(textFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-log");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mostrarPlaceholder("Carregue um arquivo .txt ou .log para visualizar...");

        // ═══════════════════════════════════════
        //  AÇÃO: Carregar Arquivo
        // ═══════════════════════════════════════
        btnCarregar.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Selecionar arquivo de Log");
            fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Arquivos de Log", "*.txt", "*.log"),
                new FileChooser.ExtensionFilter("Todos os arquivos", "*.*")
            );
            File arquivo = fc.showOpenDialog(stage);
            if (arquivo != null) {
                carregarArquivo(arquivo);
            }
        });

        // ═══════════════════════════════════════
        //  LAYOUT PRINCIPAL
        // ═══════════════════════════════════════
        VBox root = new VBox(0, barraSuperior, barraFiltros, scrollPane);
        root.getStyleClass().add("root-container");

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Log Separator");
        stage.setMinWidth(500);
        stage.setMinHeight(400);

        // Parar o watcher ao fechar a janela
        stage.setOnCloseRequest(ev -> pararWatcher());

        stage.show();
    }

    // ═══════════════════════════════════════
    //  MÉTODOS DE NEGÓCIO
    // ═══════════════════════════════════════

    /**
     * Carrega um novo arquivo e inicia o monitoramento em tempo real.
     */
    private void carregarArquivo(File arquivo) {
        // Para qualquer watcher anterior
        pararWatcher();

        // Limpa dados antigos
        logStore.limpar();
        textFlow.getChildren().clear();

        // Inicia novo watcher com callback para a UI
        watcher = new LogFileWatcher(arquivo.toPath(), novasEntradas -> {
            logStore.addEntradas(novasEntradas);

            // Atualiza a UI na thread do JavaFX
            Platform.runLater(() -> {
                atualizarVisualizacao();
                contadorLabel.setText("📊 " + logStore.tamanho() + " linhas");
            });
        });

        statusLabel.setText("📄 " + arquivo.getName());
        monitorLabel.setText("🟢 Monitorando");
        contadorLabel.setText("📊 0 linhas");
        filtroAtual = LogType.ALL;
        selecionarBotao(btnAll);

        watcher.start();
    }

    /**
     * Para o watcher atual de forma segura.
     */
    private void pararWatcher() {
        if (watcher != null) {
            watcher.parar();
            watcher = null;
            Platform.runLater(() -> monitorLabel.setText(""));
        }
    }

    // ═══════════════════════════════════════
    //  MÉTODOS DE UI
    // ═══════════════════════════════════════

    /**
     * Cria um botão de filtro com ação configurada.
     */
    private Button criarBotaoFiltro(String texto, LogType tipo) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("btn-filtro");
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);

        btn.setOnAction(e -> {
            if (logStore.tamanho() == 0) {
                mostrarPlaceholder("Carregue um arquivo de log primeiro!");
                return;
            }
            selecionarBotao(btn);
            filtroAtual = tipo;
            atualizarVisualizacao();
        });

        return btn;
    }

    /**
     * Marca visualmente o botão de filtro selecionado.
     */
    private void selecionarBotao(Button btn) {
        if (botaoSelecionado != null) {
            botaoSelecionado.getStyleClass().remove("selecionado");
        }
        btn.getStyleClass().add("selecionado");
        botaoSelecionado = btn;
    }

    /**
     * Renderiza as entradas de log no TextFlow com cores por tipo.
     */
    private void atualizarVisualizacao() {
        List<LogEntry> entradas = logStore.filtrar(filtroAtual);
        textFlow.getChildren().clear();

        if (entradas.isEmpty()) {
            mostrarPlaceholder("Nenhum log encontrado para o filtro: " + filtroAtual.name());
            return;
        }

        for (int i = 0; i < entradas.size(); i++) {
            LogEntry entry = entradas.get(i);
            String sufixo = (i < entradas.size() - 1) ? "\n" : "";
            Text textoLinha = new Text(entry.getTexto() + sufixo);
            textoLinha.getStyleClass().add("log-text");

            // Aplica a classe CSS baseada no tipo
            switch (entry.getTipo()) {
                case ERROR   -> textoLinha.getStyleClass().add("log-erro");
                case WARNING -> textoLinha.getStyleClass().add("log-warning");
                case INFO    -> textoLinha.getStyleClass().add("log-info");
                default      -> { /* NORMAL mantém estilo padrão */ }
            }

            textFlow.getChildren().add(textoLinha);
        }
    }

    /**
     * Mostra uma mensagem de placeholder na área de log.
     */
    private void mostrarPlaceholder(String mensagem) {
        textFlow.getChildren().clear();
        Text placeholder = new Text(mensagem);
        placeholder.getStyleClass().add("log-placeholder");
        textFlow.getChildren().add(placeholder);
    }
}
