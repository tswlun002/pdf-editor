# pdf-editor
Edit pdf files with functions like merge, remove pages

## Configure Kafka Brokers

### Check brokers Partition count , Replicas and Isr
```dockerfile
docker exec --interactive --tty pdf-kafka-broker-1  kafka-topics --bootstrap-server pdf-kafka-broker-1:19092 --describe --topic download-document-event

```
### Setting up min.insync.replica
```dockerfile
docker exec --interactive --tty pdf-kafka-broker-1 \
kafka-configs  --bootstrap-server pdf-kafka-broker-1:19092 --entity-type topics --entity-name  download-document-event \
--alter --add-config min.insync.replicas=2
```