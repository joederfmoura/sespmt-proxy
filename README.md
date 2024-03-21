# sespmt-proxy

# Integrando um proxy reverso traefik no projeto sesp-mt

## O que é Traefik?

Traefik é um proxy reverso de código aberto que torna a publicação de seus serviços uma experiência fácil e divertida. Ele recebe solicitações em nome do seu sistema e descobre quais componentes são responsáveis ​​por tratá-las.

O que diferencia o Traefik, além de suas inúmeras funcionalidades, é que ele descobre automaticamente a configuração certa para seus serviços. A mágica acontece quando o Traefik inspeciona sua infraestrutura, onde encontra informações relevantes e descobre qual serviço atende qual solicitação.

Traefik é nativamente compatível com todas as principais tecnologias de cluster, como Kubernetes, Docker, Docker Swarm, AWS, Mesos, Marathon, dentre outras ; e pode lidar com muitos ao mesmo tempo. (Funciona até mesmo para software legado executado em bare metal.)

Com o Traefik, não há necessidade de manter e sincronizar um arquivo de configuração separado: tudo acontece automaticamente, em tempo real (sem reinicializações, sem interrupções de conexão). Com o Traefik, você gasta tempo desenvolvendo e implantando novos recursos em seu sistema, não configurando e mantendo seu estado de funcionamento.

# Por que usar um proxy reverso

Em ambientes de contêineres, onde várias aplicações estão em execução e se comunicam dinamicamente, um proxy reverso desempenha um papel vital no gerenciamento eficiente do tráfego. Ele atua como um ponto de entrada único para as aplicações, direcionando solicitações para os contêineres apropriados com base em regras de roteamento dinâmicas. Isso simplifica a arquitetura de rede, facilita a escalabilidade horizontal e oferece uma camada adicional de segurança, ao mesmo tempo que permite a configuração e o monitoramento centralizados.

## Configuração
![traefik-architecture](https://github.com/joederfmoura/sespmt-proxy/assets/149547286/d2059423-ee41-4630-861b-6f2eb83a4048)





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

O código abaixo foi adicionado ao arquivo setup/docker-compose-tools.yml

```bash

 proxy:
    image: traefik:1.7.2-alpine
    container_name: proxy
    volumes:
      - $PWD/traefik/traefik.toml:/traefik.toml
      - /var/run/docker.sock:/var/run/docker.sock
    ports:
      - "80:80"
      - "443:443"
    labels:
      - "traefik.frontend.rule=Host:monitor.localhost"
      - "traefik.docker.network=sesp-microsservicos"
      - "traefik.port=8080"
    networks:
      - sesp-microsservicos

 ```

O código abaixo foi adicionado ao arquivo setup/docker-compose-tools.yml em cada container que receberá acesso através do proxy.

```bash

    labels:
      - "traefik.frontend.rule=Host:<endereco>.localhost"
      - "traefik.docker.network=sesp-microsservicos"
      - "traefik.port=8080"

```




## Endereços

Ao inicializar os containers utilizando este `docker-compose.yml` cada serviço será vinculado a um endereço:

| Nome do container        | Descrição                             | Porta |Endereço|
| ------------------------ | ------------------------------------- | ----- |----|
| Traefik Dashboard        | Traefik Proxy Reverso                 | 8080  |http://monitor.localhost|
| ldap-admin               | Painel de Administração do LDAP       | 3390  |http://ldap-admin.localhost|
| alertanager              |Ferramenta de alertas                  | 35432 |http://alertmanager.localhost|
| grafana                  | Plataforma de métricas em gráficos    | 3000  |http://grafana.localhost|
| promethues               | Ferramenta de Monitoramento           | 9090  |http://prometheus.localhost|
| gerenciamento-roubo-web  | Protótipo funcional angular           | 8006  |http://gerenciamento-roubo.localhost|

