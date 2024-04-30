package com.documentservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
@Profile("local")
@RequiredArgsConstructor
public class KafkaConfig {
    private  final Environment env;

    @Bean
    public NewTopic newTopic(){
        var name = env.getProperty("topics.download-document-name");
        if(name==null)throw  new RuntimeException("Download topic name is required");
        return TopicBuilder.name(name).partitions(3)
                .replicas(3).build();
    }

}
