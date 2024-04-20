package com.emailservice.kafka;
import com.sun.mail.util.MailConnectException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import java.util.List;
import java.util.function.Function;
import org.springframework.mail.MailSendException;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class ConsumerConfig {

    private  final KafkaTemplate<String,Byte[]> kafkaTemplate;
    private  final KafkaProperties kafkaProperties;
    private final Environment env;

    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configure,
            ObjectProvider<ConsumerFactory<Object,Object>> kafkaConsumer
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configure.configure(factory, kafkaConsumer.getIfAvailable(()->new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties())));
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
                   if (e.getCause() instanceof MessagingException || e.getCause() instanceof MailSendException) {
                       log.info("Recovery Exception in the recovery: {}",e.getMessage(),e);
                       return new TopicPartition(getTopic.apply("topics.retry"), r.partition());
                   }
                   else {
                       log.info("Dead Exception in the recovery: {}",e.getMessage(),e);
                       return new TopicPartition(getTopic.apply("topics.dead"), r.partition());
                   }
               });
   }

    private CommonErrorHandler myErrorHandler() {
        var expoBackOff = new ExponentialBackOffWithMaxRetries(3);
        expoBackOff.setInitialInterval(1_000L);
        expoBackOff.setMultiplier(2);
        expoBackOff.setMaxInterval(10_000L);

        var retryableErrors = List.of(MailSendException.class,com.emailservice.exception.MailSenderException.class,
                                      MailConnectException.class,MessagingException.class);

        var errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer(), expoBackOff);

       retryableErrors.forEach(errorHandler::addRetryableExceptions);

        return errorHandler;
    }
}
