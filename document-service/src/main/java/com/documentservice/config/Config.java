package com.documentservice.config;

import com.documentservice.exception.CustomErrorDecoder;
import feign.FeignException;
import feign.codec.ErrorDecoder;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;

@Configuration
public class Config {
    @Value("${rest-template.pool-size}")
    private int POOL_SIZE;
    @Value("${rest-template.timeout}")
    private int TIME_OUT;
    private final static Logger LOGGER = LoggerFactory.getLogger(Config.class);
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
    public Retry retry(RetryRegistry    registry){

        var config= RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.of(100, ChronoUnit.MILLIS),2))
                .retryOnException(e ->  e.getCause() instanceof TimeoutException || e.getCause() instanceof FeignException.ServiceUnavailable)
                .build();
        ;
        return registry.retry("users",config) ;
    }

    @Bean
    ModelMapper modelMapper(){
        return  new ModelMapper();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer(){
        return factory->factory.configureDefault(
                id->new Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(
                                TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build()
                        )
                        .build() );
    }
}
