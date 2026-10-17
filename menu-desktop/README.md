# Menu Utilidades (desktop, Java + SQLite)

## Requisitos
- Java 25 (LTS) ou superior
- Maven 3.8 ou superior

## Como rodar
1. Entre na pasta `menu-desktop`
2. Execute: `mvn compile exec:java`

Na primeira execução o Maven baixa o driver SQLite. O banco é criado automaticamente
no arquivo `menu.db`, na pasta de onde você executou o comando. Não é preciso instalar banco.

## Login e senhas
- Senhas são guardadas com PBKDF2-HMAC-SHA256 e sal aleatório, nunca em texto puro.
- "Esqueceu a senha?" gera um código de 6 dígitos válido por 15 minutos e o exibe na tela.
  Não há envio de e-mail nesta versão: o código aparece no próprio aplicativo.

## Dados
Agenda, produtos, caixa e votação são salvos no `menu.db` e separados por usuário
(a votação é global, como no original).
