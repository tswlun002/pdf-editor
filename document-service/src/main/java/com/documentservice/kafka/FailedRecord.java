package com.documentservice.kafka;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Document(collection = "FailedRecords")
public class FailedRecord {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private String id;
    private  String key;
    private  byte[] value;
    private  String topic;
    private  String exception;
    @Enumerated(EnumType.STRING)
    private  RecordStatus status;
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FailedRecord record)) return false;
        return Objects.equals(getId(), record.getId()) && Objects.equals(getTopic(), record.getTopic())
                && Objects.equals(getKey(), record.getKey()) && Arrays.equals(getValue(), record.getValue())
                && Objects.equals(getException(), record.getException())
                && getStatus() == record.getStatus() && Objects.equals(getTimestamp(), record.getTimestamp())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTopic(), getKey(), Arrays.hashCode(getValue()),
                getException(), getStatus(), getTimestamp());
    }

    @Override
    public String toString() {
        return "FailedRecord{" +
                "id='" + id + '\'' +
                ", topic='" + topic + '\'' +
                ", key='" + key + '\'' +
                ", value='" + Arrays.toString(value) + '\'' +
                ", exception='" + exception + '\'' +
                ", recordStatus=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
