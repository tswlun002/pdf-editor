package com.emailservice.kafka;

import com.emailservice.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class DownloadEventRecoveryConsumer {
    private final EmailService service;

   @KafkaListener(topics = {"${topics.retry}"},groupId = "download-document-event-listener-group")
    public void onDownloadDocument(ConsumerRecord<String, byte[]> consumerRecord){
       var email= consumerRecord.key();
       var file = consumerRecord.value();
     log.info("Download document event listener was successful, data:{}",email+ Arrays.toString(file));
     var sent = service.sendEmail(email,file);
     if(!sent){
         log.error("Internal server error.");
         throw  new InternalError("Internal server error.");
     }
     log.info("Document sent successfully to your email");
   }
}
