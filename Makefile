run:
	docker-compose -f docker-compose-db.yml up  --build --force-recreate  -d
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate  -d
run-brokers:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate  -d --remove-orphans
run-broker-1:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate pdf-kafka-1 -d
run-broker-2:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate pdf-kafka-2 -d
run-broker-3:
	docker-compose -f docker-compose-kafka.yml up  --build --force-recreate pdf-kafka-3 -d
down-brokers:
	docker-compose -f docker-compose-kafka.yml down
run-db:
	docker-compose -f docker-compose-db.yml up  --build --force-recreate  -d
down-db:
	docker-compose  -f  docker-compose-db.yml   down
clean:
	docker-compose -f docker-compose-db.yml down
	docker-compose -f docker-compose-kafka.yml down
	rm -f *.txt
