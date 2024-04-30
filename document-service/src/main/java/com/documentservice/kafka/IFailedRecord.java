package com.documentservice.kafka;
import org.springframework.stereotype.Service;
import  java.util.Set;
@Service
public interface IFailedRecord {

    void saveRecord(String key, Throwable throwable,  byte[] value, String topic, RecordStatus recordStatus,String id);

    Set<FailedRecord> findAllByStatus(RecordStatus recordStatus);

    void updateStatus(FailedRecord failedRecord1, RecordStatus recordStatus);
}
