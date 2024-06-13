package com.documentservice.scheduler;

import com.documentservice.kafka.DownloadEventProducer;
import com.documentservice.kafka.FailedRecord;
import com.documentservice.kafka.IFailedRecord;
import com.documentservice.kafka.RecordStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@RequiredArgsConstructor
@Component
public class DownloadRetryScheduler {

     private  final IFailedRecord failedRecord;
     private  final DownloadEventProducer producer;
    private static  final Logger LOGGER = LoggerFactory.getLogger(DownloadRetryScheduler.class);
     @Scheduled(fixedDelay = 300000)
    public  void retryDownload(){
         LOGGER.info("Job started running.");
         failedRecord.findAllByStatus(RecordStatus.RETRY).forEach(
                 failedRecord1 -> {
                     var record = buildRecord(failedRecord1);
                      var isdownloaded=producer.retryDownloadEvent(record,failedRecord1.getTraceId(),failedRecord1.getId());

                      if(isdownloaded){
                          failedRecord.updateStatus(failedRecord1, RecordStatus.SOLVED);
                      }
                 }
         );
         LOGGER.info("Job finished running.");

     }

    private ProducerRecord<String,byte[]> buildRecord(FailedRecord failedRecord) {
         return   new ProducerRecord<>(failedRecord.getTopic(), failedRecord.getKey(), failedRecord.getValue());
    }

}
