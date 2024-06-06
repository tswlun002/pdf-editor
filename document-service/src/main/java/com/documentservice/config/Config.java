package com.documentservice.config;

import com.documentservice.exception.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {
    @Value("${rest-template.pool-size}")
    private int POOL_SIZE;
    @Value("${rest-template.timeout}")
    private int TIME_OUT;
    @Bean
    public HttpComponentsClientHttpRequestFactory requestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.custom()
                .setConnectionManager(poolingConnectionManager())
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(POOL_SIZE);
        poolingConnectionManager.setDefaultMaxPerRoute(POOL_SIZE);
        return poolingConnectionManager;
    }
    @Bean
    public  RestTemplate restTemplate(){
       var requestFactory = requestFactory();
       requestFactory.setConnectionRequestTimeout(TIME_OUT);
       requestFactory.setConnectTimeout(TIME_OUT);
        return new RestTemplate(requestFactory);
    }
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
    @Bean
    ModelMapper modelMapper(){
        return  new ModelMapper();
    }
}
