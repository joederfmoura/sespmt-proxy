# Setup

Projeto utilizado para facilitar a configuração e deploy do ambiente de microsserviços, contendo os seguintes serviços:

- Carteira Funcional
- Gerenciamento de Ocorrências
- Gerenciamento de Patrulhas
- Gerenciamento de Procurados
- Registro Geral

## 1. Como utilizar

Para utilizar o `setup`, é necessário que os serviços estejam previamente organizados em um diretório comum, com os nomes padrões dos projetos, conforme a representação abaixo:

```shell
.
+-- autenticacao
+-- carteira-funcional
+-- gerenciamento-ocorrencia
+-- gerenciamento-patrulha
+-- gerenciamento-procurado
+-- gerenciamento-roubo-web
+-- registro-geral
+-- setup
```

### 1.1. Inicialização dos containers

Para efetuar a `build` dos projetos, pode-se utilizar o script localizado na raiz do pojeto `setup`:

```shell
sh build_and_start.sh
```

Este procedimento irá empacotar o projeto utilizando o maven, efetuar o build das imagens e inicializar os containers. A próxima vez que quiser executar o projeto, basta executar o script `start.h` que é mais rápido, visto que não cria mais as imagens, apenas inicia os contêineres.

### 1.3. Parar execução dos containers

Para parar a execução dos containers, o script `stop.sh` pode ser utilizado.

```shell
sh stop.sh
```

### 1.4. Execução dos projetos

Para executar os projetos, pode-se utilizar os arquivos `docker-compose-*.yml` localizados na raiz do projeto `setup`:

```shell
docker-compose -f docker-compose-tools.yml up -d
docker-compose -f docker-compose-apps.yml up -d
```

Para verificar a situação de cada serviço, pode-se utiliar o comando `ps`:

```shell
docker ps -a
```

Para interromper a execução dos serviços, o mesmo `docker-compose.yml` pode ser utilizado:

```shell
docker-compose -f docker-compose-tools.yml down
docker-compose -f docker-compose-apps.yml down
```

## 2. URLs dos serviços

Ao inicializar os containers utilizando este `docker-compose.yml` cada serviço será vinculado a uma porta específica:

| Nome do container        | Descrição                             | Porta |
| ------------------------ | ------------------------------------- | ----- |
| ldap-admin               | Painel de Administração do LDAP       | 3390  |
| db-keycloak              | Banco de dados PostgreSQL do Keycloak | 35432 |
| keycloak                 | Servidor de identificação             | 38080 |
| graylog                  | Ferramenta de administração de logs   | 9000  |
| autenticacao             | Serviço de Autenticação do Keycloak   | 8000  |
| carteira-funcional       | Carteira Funcional                    | 8001  |
| gerenciamento-ocorrencia | Gerenciamento de Ocorrências          | 8002  |
| gerenciamento-patrulha   | Gerenciamento de Patrulhas            | 8003  |
| gerenciamento-procurado  | Gerenciamento de Procurados           | 8004  |
| registro-geral           | Registro Geral                        | 8005  |
| gerenciamento-roubo-web  | Protótipo funcional angular           | 8006  |

## 3. Acesso ao LDAP

Para acessar o painel de administração do `LDAP` acesse a URL com a porta correspondente: `http://localhost:3390/` e  efetue o login utilizando as credenciais abaixo.
- Login DN: `cn=admin,dc=mt,dc=gov,dc=br`
- Senha: `admin`

## 4. Acesso ao Graylog
Para acessar o painel de administração do `Graylog` acesse a URL com a porta correspondente: `http://localhost:9000/` e  efetue o login utilizando as credenciais abaixo.
- Login: `admin`
- Senha: `admin`