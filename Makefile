.PHONY: build up down logs clean drop test coverage

# Build the Docker image using docker-compose
build:
	@docker compose build

# Start containers with rebuild and run in detached mode
up:
	@docker compose up --build -d

# Stop and remove containers, networks, and volumes defined in the compose file
down:
	@docker compose down

# Tail logs from all services
logs:
	@docker compose logs -f

# Remove dangling Docker images (not referenced by any tag)
clean:
	@docker image prune -f

# Drop data base tables
drop:
	@./mvnw liquibase:dropAll

# Run tests with the 'test' Spring profile
test:
	@./mvnw clean verify -Dspring.profiles.active=test

# Run tests with the 'test' Spring profile and open JaCoCo report in browser
coverage:
	@./mvnw clean verify -Dspring.profiles.active=test && xdg-open target/jacoco-report/index.html