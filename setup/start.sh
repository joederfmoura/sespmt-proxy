#!/bin/bash
# IMPORTANTE:
# A primeira vez que a aplicação é executada deve ocorrer por meio de build_and_start.sh
#
# Como usar:
# ./start.sh

URL_GRAYLOG="http://localhost:9000"

echo "Iniciando ferramentas"
docker compose -f docker-compose-tools.yml up -d

# Aguarda a inicializacao do Graylog por 3 minutos, pois e o servico mais 
# demorado para iniciar e as apps dependem dele para enviar o log
echo "Aguardando inicializacao do Graylog"
for (( TENTATIVA=1; TENTATIVA <= 30; TENTATIVA++ ))
do
  sleep 15
  
  STATUS=$(curl -Is $URL_GRAYLOG -m 5 | head -n 1)

  if [[ $STATUS == *"200"* ]]; then
    break
  else
    if [[ $TENTATIVA == 30 ]]; then
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

echo "Iniciando aplicacoes"
docker compose -f docker-compose-apps.yml up -d

echo "Setup finalizado com sucesso. As aplicacoes estarao disponiveis em alguns minutos."
