# Log Separator - Monitoramento e Classificação de Logs

![Java Version](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Build-Maven-blue)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-green)

Este projeto é uma aplicação JavaFX para monitoramento em tempo real e classificação de arquivos de log. Ele separa automaticamente entradas de log por gravidade (`ERROR`, `WARNING`, `INFO`).

## 🎯 Objetivo da Atividade (C14)
Este repositório foi desenvolvido para a atividade prática de **Engenharia de Software (C14)** no Inatel, focando na implementação de um pipeline completo de **CI/CD** com testes automatizados.

## 🚀 Pipeline CI/CD
O pipeline está configurado via **GitHub Actions** (`.github/workflows/main.yml`) e contempla os seguintes estágios:

1.  **Testes Unitários**: Execão de 20 cenários de teste cobrindo fluxos normais e de exceção.
2.  **Build & Package**: Compilação e geração do artefato JAR de forma paralela aos testes.
3.  **Deploy**: Criação automática de um GitHub Release com o JAR compilado.
4.  **Notificação**: Script Python (`scripts/notify.py`) que reporta o status final do pipeline (suporta Webhooks de Discord/Slack).

## 🧪 Testes Automatizados
O projeto possui **20 cenários de teste unitário** utilizando JUnit 5:
- **LogParserTest**: 10 cenários focados na lógica de classificação (Regex, Case-insensitivity, prioridades).
- **LogStoreTest**: 10 cenários focados no gerenciamento de dados em memória e filtragem.

Para rodar os testes localmente:
```bash
mvn test
```

## 🛠️ Tecnologias
- **Java 17**
- **JavaFX** (Interface Gráfica)
- **JUnit 5** (Testes)
- **Maven** (Gerenciamento de dependências e build)
- **Python 3** (Script de notificação)

## 💡 IA?
Para esta atividade, a **IA Antigravity (Google DeepMind)** foi utilizada como assistente de programação (Pair Programming).
- **Prompts Utilizados**: O arquivo PDF com os requisitos foi fornecido à IA para elaboração do plano de testes e estrutura do pipeline.
- **Resultado**: A IA auxiliou na organização estrutural, criação dos 20 casos de teste baseados na lógica de negócio existente e na escrita do workflow YAML seguindo as melhores práticas de DevOps. O resultado foi satisfatório, garantindo a cobertura exigida e a automação do deploy.

---
**Professor**: Christopher Lima  
**Instituição**: Inatel (Instituto Nacional de Telecomunicações)
