# pdf-editor
Edit pdf files with functions like merge, remove pages

## Configure Kafka Brokers

### Check brokers Partition count , Replicas and Isr
```dockerfile
docker exec --interactive --tty pdfKafkaBroker1 kafka-topics --bootstrap-server pdfKafkaBroker1:19092 --describe --topic download-document-event

```
### Setting up min.insync.replica
```dockerfile
docker exec --interactive --tty pdfKafkaBroker1 \
kafka-configs  --bootstrap-server pdfKafkaBroker1:19092 --entity-type topics --entity-name  download-document-event \
--alter --add-config min.insync.replicas=2
```

## Refresh end-point after update app properties

### When app run on cloud domain

```angular2html
 http://pdf-editor/8071/monitor
```
### App runs on localhost
#### Update user service
```
 http://localhost:8080/actuator/refresh 
```
#### Update email service
```
 http://localhost:8083/actuator/refresh 
```
#### Update document service
```
 http://localhost:8082/actuator/refresh 
```