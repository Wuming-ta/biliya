#!/bin/sh
COMPOSE_DOCKER_CLI_BUILD=1 DOCKER_BUILDKIT=1 docker-compose -f docker-compose.buildwar.yml up --build  # --no-cache
