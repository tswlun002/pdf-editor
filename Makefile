run:
	docker-compose -f docker-compose-hook.yml   up  -d;
	docker-compose -f docker-compose-db.yml up  -d;
	docker-compose -f docker-compose-kafka.yml up -d;
stop-iternal-services:
	systemctl stop mongod.service
	systemctl stop mysql.service
run-hooks:
	docker-compose -f docker-compose-hook.yml   up  -d
	echo "DO NOT FORGET TO SET UP LISTENING PORT AND PATH FOR WEB HOOK!!!!!"
run-brokers:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate  -d
run-broker-1:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate pdf-kafka-broker-1 -d
run-broker-2:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate pdf-kafka-broker-2 -d
run-broker-3:
	docker-compose -f docker-compose-kafka.yml up	pdf-kafka-broker-3	-d
run-config-broker:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate config-broker -d
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
