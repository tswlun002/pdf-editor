package com.documentservice.kafka;

import com.documentservice.exception.InternalServerError;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailedRecordService implements  IFailedRecord{
    private  final  FailedRecordRepository repository;

    @Override
    public void saveRecord(String key, Throwable throwable,  byte[] value, String topic, RecordStatus recordStatus,
                           String id) {
        if(StringUtils.isBlank(key)||recordStatus==null){
            log.error("Invalid FailedRecord details. key: {}, topic:{}, status:{}",key,topic,recordStatus);
            throw  new InternalServerError("Invalid FailedRecord details.");
        }

        try {
            if (id==null|| repository.findById(id).isEmpty()){
                FailedRecord record =
                        FailedRecord.builder().value(value)
                                .status(recordStatus).topic(topic)
                                .exception(throwable.toString())
                                .key(key)
                                .timestamp(LocalDateTime.now())
                                .build();

                repository.save(record);
            }
        }catch (Exception e){
            log.error("Error ---------> {}",e.getMessage(),e);
        }
    }

    @Override
    public Set<FailedRecord> findAllByStatus(RecordStatus recordStatus) {
        return repository.findAllByStatus(recordStatus.name());
    }

    @Override
    public void updateStatus(FailedRecord failedRecord1, RecordStatus recordStatus) {
          if(failedRecord1==null) throw  new InternalServerError("Invalid FailedRecord can not be updated");
          if(recordStatus==null)  throw  new InternalServerError("Invalid RecordStatus for  updating FailedRecord");
          try{
              failedRecord1.setStatus(recordStatus);
              repository.save(failedRecord1);
          }catch (Exception e){
              log.error("Internal server error:{}",e.getMessage(),e);
              throw  new InternalServerError("Internal server error");
          }
    }
}
