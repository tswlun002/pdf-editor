run:
	docker-compose -f docker-compose-kafka.yml up -d --build;
	docker-compose -f docker-compose-servers.yml up -d --build;
stop-iternal-services:
	systemctl stop mongod.service
	systemctl stop mysql.service
run-brokers:
	docker-compose -f docker-compose-kafka.yml up  --build   -d
run-broker-1:
	docker-compose -f docker-compose-kafka.yml up  --build  pdf-kafka-broker-1 -d
run-broker-2:
	docker-compose -f docker-compose-kafka.yml up  --build pdf-kafka-broker-2 -d
run-broker-3:
	docker-compose -f docker-compose-kafka.yml up	pdf-kafka-broker-3	-d
config-broker:
	docker-compose -f docker-compose-kafka.yml up  --build  config-broker -d
config-server:
	docker-compose -f docker-compose-servers.yml up --build configServerApp -d
gateway-server:
	docker compose -f docker-compose-servers.yml up --build gatewayServerApp -d
document-app:
	docker-compose -f docker-compose-servers.yml up --build documentsServerApp -d
email-app:
	docker-compose -f docker-compose-servers.yml up --build emailServerApp -d
run-user-app:
	docker-compose -f docker-compose-servers.yml up --build usersServerApp -d
down-config-broker:
	docker-compose -f docker-compose-kafka.yml down  config-broker
down-brokers:
	docker-compose -f docker-compose-kafka.yml down
run-db:
	docker-compose -f docker-compose-db.yml up  --build --force-recreate  -d
down-db:
	docker-compose  -f  docker-compose-db.yml   down
run-config-container:
	docker-compose	-f	docker-compose-servers.yml	up	--build	--force-recreate	-d
clean:
	docker-compose -f docker-compose-db.yml down --remove-orphans 
	docker-compose -f docker-compose-kafka.yml down --remove-orphans 
	docker-compose -f docker-compose-hook.yml down --remove-orphans 
	rm -f *.txt
