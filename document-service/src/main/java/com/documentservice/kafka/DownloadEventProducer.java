package com.documentservice.kafka;

import com.documentservice.exception.InternalServerError;
import com.documentservice.pdf.DownLoadDocumentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.NotEnoughReplicasException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@Component
public class DownloadEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final Environment env;
    private  final  IFailedRecord failedRecord;
    private static  final Logger LOGGER = LoggerFactory.getLogger(DownloadEventProducer.class);


   public boolean sendDownloadEvent(DownLoadDocumentEvent event){
        var key = event.email();
        var value= event.file();
        var traceId =event.traceId();

        List<Header> recordHeader= List.of(new RecordHeader("event-source","scanner".getBytes()));
        var topic = env.getProperty("spring.kafka.topic");
        if(topic==null || topic.isBlank())
        {
            LOGGER.error("Error: Kafka topic name is not provided. trace-Id: {}",traceId);
            throw new InternalServerError("Internal sever error.");
        }
        var producer= new ProducerRecord<>(topic,null,key,value,recordHeader);

       AtomicReference<AtomicBoolean> response = new AtomicReference<>(null);
       sendMessage(traceId,key,value,producer,response,null);
       while(response.get()==null){};
       return response.get().get()  ;
   }
   
   private  void sendMessage(String traceId,String key, byte[] value,ProducerRecord<String, byte[]>producer,AtomicReference<AtomicBoolean> response,String failedRecordId){
       var future = kafkaTemplate.send(producer);
       LOGGER.info("Produce download event to email service. trace-id: {}",traceId);
       future.whenComplete((sendResult,throwable)->{
           if(throwable!=null){
               response.set(new AtomicBoolean(false));
               handleFailure(traceId,key,throwable,value,producer.topic(),failedRecordId);
           }else{
               response.set(new AtomicBoolean(true));
               handleSuccess(traceId,key,value,sendResult);
           }
       });
       
   }

    private void handleSuccess(String traceId,String key, byte[] value, SendResult<String,byte[]> sendResult) {
       LOGGER.info("Document sent successfully to your email address: {}, document:{}, trace-Id: {}",key,value,traceId);
    }

    private void handleFailure(String traceId,String key, Throwable throwable,  byte[] value, String topic,String failedRecordId) {
        LOGGER.error("Save error topic: {} by email address: {}, trace-id: {}",topic,key,traceId,throwable);
        if(throwable.getCause() instanceof NotEnoughReplicasException || throwable.getCause() instanceof TimeoutException
        ){

            failedRecord.saveRecord( traceId,key,  throwable, value,topic, RecordStatus.RETRY,failedRecordId);
        }
        else {
            failedRecord.saveRecord( traceId,key,  throwable, value,topic, RecordStatus.DEAD,failedRecordId);
            throw  new InternalServerError("Internal sever error.");
        }

    }


    public boolean retryDownloadEvent(ProducerRecord<String,byte[]> record,String failedRecordId, String traceId) {
       LOGGER.info("Retry download event. trace-Id: {}",traceId);
       if(record==null)throw new InternalServerError("Invalid Producer Record for the retry download event");
       AtomicReference<AtomicBoolean> response = new AtomicReference<>(null);
       sendMessage(traceId,record.key(), record.value(), record,response,failedRecordId);
       while(response.get()==null);
       
       return response.get().get();
    }
}
