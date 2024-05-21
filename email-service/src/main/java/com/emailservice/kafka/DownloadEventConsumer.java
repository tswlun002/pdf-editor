package com.emailservice.kafka;

import com.emailservice.email.EmailService;
import com.emailservice.exception.MailSenderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class DownloadEventConsumer {
    private final EmailService service;
    private final Environment env;

   @KafkaListener(topics = {"download-document-event"},groupId = "download-document-event-listener-group")
    public void onDownloadDocument(ConsumerRecord<String, byte[]> consumerRecord) throws MailSenderException {
       var email= consumerRecord.key();
       var file = consumerRecord.value();
     log.info("Download document event listener was successful, data:{}",email+ Arrays.toString(file));
     var sent = service.sendEmail(email,file);
     if(!sent){
         var message = env.getProperty("download.message.fail");
         log.error(message);
         throw  new InternalError(message);
     }
     log.info(env.getProperty("download.message.successful"));
   }
}
