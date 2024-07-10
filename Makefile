SHELL := /bin/bash
.PHONY: test install run help

APP_NAME="spring-boot3-monolith"

help: ## Show this help message.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m\033[0m\n"} /^[$$()% a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

test: ## Execute all test
	@mvn clean verify

run-app: ## Run app with maven
	@mvn clean spring-boot:run

start-app-with-docker-image: ## Run app with docker (don't forget to build the image locally before)
	@docker run --net host -e SPRING_DATASOURCE_URL="jdbc:postgres://localhost:5432/monolith" ${APP_NAME}

start-app: ## Run app with docker compose
	@docker compose up -d
	@docker compose logs -f ${APP_NAME}

start-infra: ## Run required infrastructure with docker compose
	$(MAKE) kill start-database start-redis start-rabbitmq

restart-infra: ## Reset and start required infrastructure with docker compose
	$(MAKE) start-database start-redis start-rabbitmq

start-all: ## Run all containers with docker compose
	$(MAKE) start-infra start-app

restart-all: ## Restart containers with docker compose
	@docker compose stop ${APP_NAME}
	@docker compose up -d
	@docker compose logs -f ${APP_NAME}

start-database: ## Run app database
	@docker compose up -d postgres --wait

kill-database: ## Kill app database
	@docker compose rm -sf postgres
	@docker volume rm -f postgres_data

start-redis : ## Run redis
	@docker compose up -d redis

kill-redis : ## Kill redis
	@docker compose rm -sf redis
	@docker volume rm -f redis_data

start-rabbitmq : ## Run rabbitmq
	@docker compose up -d rabbitmq

kill-rabbitmq : ## Kill rabbitmq
	@docker compose rm -sf rabbitmq
	@docker volume rm -f rabbitmq_data

kill: ## Kill and reset project
	@docker compose down
	@mvn install -DskipTests
	$(MAKE) kill-database kill-redis kill-rabbitmq
	@docker system prune -f --volumes

feature: ## Create feature
	./scripts/create_feature.sh

hotfix: ## Create hotfix
	./scripts/create_hotfix.sh
