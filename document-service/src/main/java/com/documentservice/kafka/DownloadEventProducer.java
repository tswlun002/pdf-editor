package com.documentservice.kafka;

import com.documentservice.exception.InternalServerError;
import com.documentservice.pdf.DownLoadDocumentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
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


   public boolean sendDownloadEvent(DownLoadDocumentEvent event){
        var key = event.email();
        var value= event.file();

        List<Header> recordHeader= List.of(new RecordHeader("event-source","scanner".getBytes()));
        var topic = env.getProperty("spring.kafka.topic");
        if(topic==null || topic.isBlank())
        {
            log.error("Error: Kafka topic name is not provided");
            throw new InternalServerError("Internal sever error.");
        }
        var producer= new ProducerRecord<>(topic,null,key,value,recordHeader);
       AtomicReference<AtomicBoolean> response = new AtomicReference<>(null);

       var future = kafkaTemplate.send(producer);
         future.whenComplete((sendResult,throwable)->{
            if(throwable!=null){
                response.set(new AtomicBoolean(false));
                handleFailure(key,value,throwable);
            }else{
                response.set(new AtomicBoolean(true));
                handleSuccess(key,value,sendResult);
            }
        });
         while(response.get()==null){};
         return response.get().get()  ;
   }

    private void handleSuccess(String key, byte[] value, SendResult<String,byte[]> sendResult) {
       log.info("Document sent successfully to your email address: {}, document:{}",key,value);
    }

    private void handleFailure(String key, byte[] value, Throwable throwable) {
        log.info("Error sending document to your email address: {}",key);
        log.error("Internal error",throwable);
        throw  new InternalServerError("Internal sever error.");
    }


}
