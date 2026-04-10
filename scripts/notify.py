import os
import sys
import requests

def send_notification():
    """
    Envia uma notificação baseada no status do pipeline.
    Utiliza váriaveis de ambiente do GitHub Actions.
    """
    
    # Recupera variáveis do ambiente
    status = os.getenv("JOB_STATUS", "Unknown")
    workflow = os.getenv("GITHUB_WORKFLOW", "N/A")
    repo = os.getenv("GITHUB_REPOSITORY", "N/A")
    run_id = os.getenv("GITHUB_RUN_ID", "N/A")
    webhook_url = os.getenv("NOTIFY_WEBHOOK_URL") # Secret no GitHub
    
    emoji = "✅" if status == "success" else "❌"
    message = f"{emoji} *Pipeline CI/CD Status: {status.upper()}*\n"
    message += f"📂 Repositório: {repo}\n"
    message += f"🚀 Workflow: {workflow}\n"
    message += f"🔗 Detalhes: https://github.com/{repo}/actions/runs/{run_id}"

    print("-" * 30)
    print("LOG DE NOTIFICAÇÃO:")
    print(message)
    print("-" * 30)

    if webhook_url:
        try:
            # Exemplo disparando para um Webhook de Discord
            payload = {"content": message}
            response = requests.post(webhook_url, json=payload, timeout=10)
            if response.status_code == 204:
                print("Notificação enviada com sucesso para o Webhook.")
            else:
                print(f"Falha ao enviar notificação. Status: {response.status_code}")
        except Exception as e:
            print(f"Erro ao disparar webhook: {e}")
    else:
        print("Aviso: NOTIFY_WEBHOOK_URL não configurada. Notificação apenas no console.")

if __name__ == "__main__":
    send_notification()
