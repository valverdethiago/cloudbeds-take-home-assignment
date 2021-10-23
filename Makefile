dev-start:
	docker-compose -f ./docker/docker-compose.yml  up -d 

dev-stop:
	docker-compose -f ./docker/docker-compose.yml  down 

stack-start:
	docker-compose -f ./docker/docker-compose-full.yml  up -d --force-recreate --build

stack-stop:
	docker-compose -f ./docker/docker-compose-full.yml  down 

build:
	mvn clean install

run-api:
	cd user-api;\
	mvn spring-boot:run

run-consumer:
	cd user-consumer;\
	mvn spring-boot:run

.PHONY: dev-start dev-stop stack-start stack-stop build run-api run-consumer