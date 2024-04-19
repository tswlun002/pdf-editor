package com.emailservice.kafka;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import java.util.List;
import java.util.function.Function;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class ConsumerConfig {

    private  final KafkaTemplate<String,Byte[]> kafkaTemplate;
    private final Environment env;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(2);
        factory.setCommonErrorHandler(myErrorHandler());

        return factory;
    }
   public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(){
       return new DeadLetterPublishingRecoverer(kafkaTemplate,
               (r, e) -> {
                   log.info("Exception in the recovery: {}",e.getMessage(),e);
                   Function<String,String> getTopic= (envVariable)->{
                       var topic = env.getProperty(envVariable);
                       if(topic==null) throw new RuntimeException("Topic for retry recovering is null");
                       return topic;
                   };
                   if (e instanceof MessagingException) {
                       return new TopicPartition(getTopic.apply("topics.retry"), r.partition());
                   }
                   else {

                       return new TopicPartition(getTopic.apply("topics.dead"), r.partition());
                   }
               });
   }

    private CommonErrorHandler myErrorHandler() {
        var expoBackOff = new ExponentialBackOffWithMaxRetries(10);
        expoBackOff.setInitialInterval(1_000L);
        expoBackOff.setMultiplier(2);
        expoBackOff.setMaxInterval(10_000L);

        var retryableErrors = List.of(MessagingException.class);

        var errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer(), expoBackOff);

        retryableErrors.forEach(errorHandler::addNotRetryableExceptions);

        return errorHandler;
    }
}
