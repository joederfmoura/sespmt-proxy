#!/bin/bash
# Executa o build dos servicos na branch informada e inicia os containers na ordem correta.
#
# Como usar:
# ./build_and_start.sh
# 
# Servicos executados:
# - db-apps
# - ldap
# - ldap-admin
# - db-keycloak
# - keycloak
# - redis
# - rabbitmq
# - mongodb-graylog
# - elasticsearch-graylog
# - graylog
# - gerenciamento-roubo-web
# - autenticacao
# - carteira-funcional
# - gerenciamento-ocorrencia
# - gerenciamento-procurado
# - gerenciamento-patrulha
# - registro-geral

SERVICOS=(carteira-funcional gerenciamento-ocorrencia gerenciamento-procurado gerenciamento-patrulha registro-geral)
PREFIXO_IMAGEM="br.gov.mt.sesp"
URL_GRAYLOG="http://localhost:9000"

echo "Criando volumes para o banco de dados"
docker volume create db-apps-volume &> /dev/null
docker volume create db-keycloak-volume &> /dev/null

echo "Criando rede sesp-microsservicos"
docker network remove sesp-microsservicos &> /dev/null
docker network create sesp-microsservicos &> /dev/null

echo "Construindo imagens das ferramentas"

echo "Construindo a imagem do ldap"
cd ldap
docker build -t br.gov.mt.sesp/ldap --no-cache . &> /dev/null
cd ..

echo "Construindo imagem do banco de dados das aplicacoes"
cd db-apps
docker build -t br.gov.mt.sesp/db-apps --no-cache . &> /dev/null
cd ..

echo "Construindo imagem do banco de dados do keycloak"
cd db-keycloak
docker build -t br.gov.mt.sesp/db-keycloak --no-cache . &> /dev/null
cd ..

echo "Construindo imagem do keycloak"
cd keycloak
docker build -t br.gov.mt.sesp/keycloak --no-cache . &> /dev/null
cd ..

echo "Construindo imagem da autenticacao"
cd ../autenticacao
rm -fr ./target
sh mvnw clean package &> /dev/null
mv ./target/*.jar ./target/app.jar
docker build -t br.gov.mt.sesp/autenticacao --no-cache . &> /dev/null
rm -fr ./target
cd ..

echo "Construindo a imagem do Spring Boot Admin"
cd spring-boot-admin
rm -rf ./target
sh mvnw clean package &> /dev/null
mv ./target/*.jar ./target/app.jar
docker build -t br.gov.mt.sesp/spring-boot-admin --no-cache . &> /dev/null
rm -fr ./target
cd ..

echo "Iniciando ferramentas"
cd setup
docker compose -f docker-compose-tools.yml up -d

# Aguarda a inicializacao do Graylog por 3 minutos, pois e o servico mais 
# demorado para iniciar e as apps dependem dele para enviar o log
echo "Aguardando inicializacao das ferramentas"
for (( TENTATIVA=1; TENTATIVA <= 12; TENTATIVA++ ))
do
  sleep 15
  
  STATUS=$(curl -Is $URL_GRAYLOG -m 5 | head -n 1)

  if [[ $STATUS == *"200"* ]]; then
    break
  else
    if [[ $TENTATIVA == 12 ]]; then
      echo "Erro na inicializacao das ferramentas. Encerrando setup."

      docker compose -f docker-compose-tools.yml down

      exit 1
    else
      echo "."
    fi
  fi
done

echo "Criando input no Graylog"
if [ `curl -s -u admin:admin -H 'Content-Type: application/json' -X GET 'http://localhost:9000/api/system/inputs' | grep -c 'GELF_UDP'` == 0 ]
then
  curl -u admin:admin -H 'Content-Type: application/json' -X POST 'http://localhost:9000/api/system/inputs' -d '{
    "title": "GELF_UDP",
    "type": "org.graylog2.inputs.gelf.udp.GELFUDPInput",
    "global": true,
    "configuration": {
      "recv_buffer_size": 1048576,
      "tcp_keepalive": false,
      "use_null_delimiter": true,
      "number_worker_threads": 2,
      "tls_client_auth_cert_file": "",
      "bind_address": "0.0.0.0",
      "tls_cert_file": "",
      "decompress_size_limit": 8388608,
      "port": 12201,
      "tls_key_file": "",
      "tls_enable": false,
      "tls_key_password": "",
      "max_message_size": 2097152,
      "tls_client_auth": "disabled",
      "override_source": null
    },
    "node": null
  }' -H 'X-Requested-By: cli'

  echo " -> Input GELF_UDP criado"
else
  echo "Input GELF_UDP já existe"
fi

cd ..

echo "Construindo imagens das aplicacoes"
echo "Iniciando build do servico gerenciamento-roubo-web"
cd gerenciamento-roubo-web
IMAGEM_PROTOTIPO=$PREFIXO_IMAGEM/gerenciamento-roubo-web

echo "Criando imagem"
docker build -t $IMAGEM_PROTOTIPO:latest --no-cache .

cd ..

echo "Build do servico gerenciamento-roubo-web efetuado com sucesso"

for SERVICO in "${SERVICOS[@]}"
do
  echo "Iniciando build do servico $SERVICO"
  cd $SERVICO

  echo "Efetuando build do projeto maven"
  rm -fr ./target
  sh mvnw clean package &> /dev/null
  mv ./target/*.jar ./target/app.jar

  echo "Criando imagem do servico $SERVICO"
  NOME_IMAGEM=$PREFIXO_IMAGEM/$SERVICO
  docker build -t $NOME_IMAGEM:latest --no-cache . &> /dev/null

  echo "Limpando arquivos"
  rm -fr target

  echo "Build do servico $SERVICO efetuado com sucesso"

  cd ..
done

echo "Iniciando aplicacoes"
cd setup
docker compose -f docker-compose-apps.yml up -d

echo "Limpando imagens nao utilizadas"
docker image prune -f

echo "Setup finalizado com sucesso. As aplicacoes estarao disponiveis em alguns minutos."
