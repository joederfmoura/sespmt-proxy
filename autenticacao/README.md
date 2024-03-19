# Autenticação

O projeto **Autenticação** funciona como um *wrapper* para as chamadas ao Keycloak. Esta aplicação foi construída com o intuito de evitar o problema do *token* gerado com um *issuer* diferente do consumidor.

## Endpoints

| Nome    | Endpoint                         |
| ---------------- | -------------------------------- |
| Login            | `/autenticacao/keycloak/login`   |
| Refresh          | `/autenticacao/keycloak/refresh` |
| Info             | `/autenticacao/keycloak/info`    |
| Login Google     | `/autenticacao/google/login`     |