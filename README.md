# sespmt-proxy

# Integrando um proxy reverso traefik no projeto sesp-mt

Traefik é um proxy reverso de código aberto que torna a publicação de seus serviços uma experiência fácil e divertida. Ele recebe solicitações em nome do seu sistema e descobre quais componentes são responsáveis ​​por tratá-las.

O que diferencia o Traefik, além de suas inúmeras funcionalidades, é que ele descobre automaticamente a configuração certa para seus serviços. A mágica acontece quando o Traefik inspeciona sua infraestrutura, onde encontra informações relevantes e descobre qual serviço atende qual solicitação.

Traefik é nativamente compatível com todas as principais tecnologias de cluster, como Kubernetes, Docker, Docker Swarm, AWS, Mesos, Marathon, dentre outras ; e pode lidar com muitos ao mesmo tempo. (Funciona até mesmo para software legado executado em bare metal.)

Com o Traefik, não há necessidade de manter e sincronizar um arquivo de configuração separado: tudo acontece automaticamente, em tempo real (sem reinicializações, sem interrupções de conexão). Com o Traefik, você gasta tempo desenvolvendo e implantando novos recursos em seu sistema, não configurando e mantendo seu estado de funcionamento.

# Por que usar um proxy reverso

Em ambientes de contêineres, onde várias aplicações estão em execução e se comunicam dinamicamente, um proxy reverso desempenha um papel vital no gerenciamento eficiente do tráfego. Ele atua como um ponto de entrada único para as aplicações, direcionando solicitações para os contêineres apropriados com base em regras de roteamento dinâmicas. Isso simplifica a arquitetura de rede, facilita a escalabilidade horizontal e oferece uma camada adicional de segurança, ao mesmo tempo que permite a configuração e o monitoramento centralizados.

## Configuração

Para integrar o traefik ao projeto, foi criado o arquivo setup/traefik/traefik.toml contendo as configurações do traefik.

```bash
  # Define os pontos de entrada padrão como HTTP e HTTPS
defaultEntryPoints = ["http", "https"]

# Configura os pontos de entrada
[entryPoints]
  # Ponto de entrada para o painel de controle (dashboard)
  [entryPoints.dashboard]
    address = ":8080"  # Porta para acessar o painel de controle
    # Configura autenticação básica para o painel de controle
    [entryPoints.dashboard.auth]
      [entryPoints.dashboard.auth.basic]
        users = ["admin:$apr1$Oj6kVYUo$IoGdDt1iQCZjinYrZlhpa."]  # Usuário e senha para autenticação
  # Ponto de entrada HTTP padrão
  [entryPoints.http]
    address = ":80"  # Porta padrão para HTTP
  # Ponto de entrada HTTPS padrão
  [entryPoints.https]
    address = ":443"  # Porta padrão para HTTPS
    [entryPoints.https.tls]  # Configuração TLS para HTTPS

# Configuração da API do Traefik
[api]
entrypoint="dashboard"  # Define o ponto de entrada para acessar a API como o painel de controle

# Configuração para monitoramento de contêineres Docker
[docker]
domain = "localhost"  # Domínio para os contêineres Docker
watch = true  # Habilita a monitoração automática de contêineres Docker
network = "sesp-microsservicos"  # Rede Docker onde os contêineres estão conectados
```



## Endereços

Ao inicializar os containers utilizando este `docker-compose.yml` cada serviço será vinculado a um endereço:

| Nome do container        | Descrição                             | Porta |Endereço|
| ------------------------ | ------------------------------------- | ----- |----|
| Traefik Dashboard        | Traefik Proxy Reverso                 | 8080  |https://monitor.localhost|
| ldap-admin               | Painel de Administração do LDAP       | 3390  |https://ldap-admin.localhost|
| db-keycloak              | Banco de dados PostgreSQL do Keycloak | 35432 |https://alertmanager.localhost|
| grafana                  | Plataforma de métricas em gráficos    | 3000  |https://grafana.localhost|
| promethues               | Ferramenta de Monitoramento           | 9090  |https://prometheus.localhost|
| gerenciamento-roubo-web  | Protótipo funcional angular           | 8006  |https://gerenciamento-roubo.localhost|

