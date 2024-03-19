# Registro Geral

Serviço responsável pela manutenção do registro geral da SESP-MT.

## 1. Informações técnicas

As tecnologias e dependências compatíveis estão listadas a seguir, bem como a versão correspondente a cada uma delas.

### 1.1 Tecnologias

| Tecnologia  | Versão | Descrição                                                       |
| ----------- | ------ | --------------------------------------------------------------- |
| Java        | 11     | Linguagem de programação.                                       |
| Spring Boot | 2.3.3  | Framework para desenvolvimento Java.                            |
| Maven       | 3.x    | Ferramenta para gerencimaneto de pacotes e execução de scripts. |

### 1.2 Dependências

| Dependência                    | Versão | Descrição                                                                                                                                  |
| ------------------------------ | ------ | ------------------------------------------------------------------------------------------------------------------------------------------ |
| Spring Boot Starter Web        | 2.3.3  | Adiciona a capacidade de construção de API's REST, utilizando Spring MVC, executado com base no Apache Tomcat por padrão.                  |
| Spring Boot Starter Validation | 2.3.3  | Adiciona a capacidade de validação de entidades ao projeto por meio de anotações. Utilizado para validação dos dados de entrada, nos DTOs. |
| Spring Boot DevTools           | 2.3.3  | Adiciona a capacidade de atualização automática do código após uma alteração com o objetivo de agilizar o desenvolvimento.                 |
| Apache Commons Lang            | 3.11   | Facilita o desenvolvimento de rotinas comuns. Utilizada para construção dos métodos **toString()** dos modelos.                            |
| ModelMapper                    | 2.3.8  | Facilita a conversão de valores (de/para) entre dois objetos. Utilizado nas conversões de DTOs para modelos e vice-versa.                  |

## 2. Endpoints

| URI                              | Método | Descrição                          |
| -------------------------------- | ------ | ---------------------------------- |
| /pessoas                         | GET    | listagem de pessoas                |
| /pessoas                         | POST   | cadastro de pessoa                 |
| /pessoas/{id}                    | GET    | busca de pessoa pelo identificador |
| /pessoas/{id}                    | PUT    | alteração de pessoa                |
| /cidades                         | GET    | listagem de cidades                |
| /cidades/{id}                    | GET    | busca de cidade pelo identificador |
| /cidades/{idCidade}/bairros      | GET    | listagem de bairros                |
| /cidades/{idCidade}/bairros/{id} | GET    | busca de bairro pelo identificador |

## 3. Build

O processo de *build* do projeto pode ser feito por build local no ambiente de desenvolvimento ou utilizando [Docker](https://www.docker.com/).

### 3.1 Build local

Para efetuar o *build* local é necessário ter o **Java** instalado na versão correspondente. 

1. Execute a build do projeto utilizando o *wrapper* do Maven incorporado no projeto:

```shell
./mvnw clean package
```

2. Execute o comando abaixo no arquivo gerado no diretório `target`, substituindo o nome do arquivo pelo nome correspondente gerado pela *build*:

```shell
java -jar target/registro-geral.jar
```

### 3.2 Build com *Docker*

1. Execute o arquivo `build.sh` para criar a imagem da aplicação:

```shell
sh build.sh
```

2. Inicie o container da aplicação:

```shell
docker run -d -p 8080:8080 --name registro-geral br.gov.mt.sesp/registro-geral
```