package com.documentservice.kafka;

import com.documentservice.exception.InternalServerError;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FailedRecordService implements  IFailedRecord{
    private  final  FailedRecordRepository repository;
    private static  final Logger LOGGER = LoggerFactory.getLogger(FailedRecordService.class);


    @Override
    public void saveRecord(String traceId,String key, Throwable throwable,  byte[] value, String topic, RecordStatus recordStatus,
                           String id) {
        if(StringUtils.isBlank(key)||recordStatus==null){
            LOGGER.error("Invalid FailedRecord details. key: {}, topic:{}, status:{}, trace-Id: {}",key,topic,recordStatus,traceId);
            throw  new InternalServerError("Invalid FailedRecord details.");
        }

        try {
            if (id==null|| repository.findById(id).isEmpty()){
                FailedRecord record =
                        FailedRecord.builder().value(value)
                                .status(recordStatus).topic(topic)
                                .exception(throwable.toString())
                                .key(key)
                                .traceId(traceId)
                                .timestamp(LocalDateTime.now())
                                .build();

                repository.save(record);
                LOGGER.info("Saved FailedRecord: {}, trace-id: {}", record,traceId);
            }
        }catch (Exception e){
            LOGGER.error("Internal server error. trace-id: {}",traceId,e);
            throw new InternalServerError("Internal server error.");
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
              LOGGER.info("Updated failed record status, record: {}, trace-Id: {}",failedRecord1,failedRecord1.getTraceId());

        }catch (Exception e){
              LOGGER.error("Internal server error:{}, trace-id: {}",e.getMessage(),failedRecord1.getTraceId(),e);
              throw  new InternalServerError("Internal server error");
          }
    }
}
