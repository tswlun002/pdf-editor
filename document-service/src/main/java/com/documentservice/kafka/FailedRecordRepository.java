package com.documentservice.kafka;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FailedRecordRepository extends MongoRepository<FailedRecord,String> {
    Set<FailedRecord> findAllByStatus(String status);
}
