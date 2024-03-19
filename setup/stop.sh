#!/bin/bash
# Para os servicos inicializados no setup

docker compose -f docker-compose-apps.yml down
docker compose -f docker-compose-tools.yml down