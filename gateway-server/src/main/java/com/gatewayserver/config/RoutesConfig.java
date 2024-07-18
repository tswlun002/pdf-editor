package com.gatewayserver.config;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.function.Function;

@Configuration
public class RoutesConfig {



    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes().
                route(predicateSpec -> predicateSpec.path("/pdf-editor/users/**").
                        filters(filterSpec->
                        filterSpec.rewritePath("/pdf-editor/users/(?<segment>.*)", "/pdf-editor/users/${segment}")
                                    .circuitBreaker(circuitConfi ->
                                            circuitConfi.setName("userServiceCircuitBreaker")
                                                    .setFallbackUri("/pdf-editor/fallback/contact-team")
                                    )
                        ).uri("lb://USERS"))
                .route(
                        buildRoute("/pdf-editor/documents/**","/pdf-editor/documents/(?<segment>.*)",
                                "/pdf-editor/documents/${segment}","documentsCircuitBreaker"
                                ,"/pdf-editor/fallback/contact-team","lb://DOCUMENTS")
                )
                .route(buildRoute("/pdf-editor/emails/**","/pdf-editor/emails/(?<segment>.*)",
                                "/pdf-editor/emails/${segment}","emailCircuitBreaker"
                                ,"/pdf-editor/fallback/contact-team","lb://EMAILS")

                )
                .route(buildRoute("/pdf-editor/configserver/**","/pdf-editor/configserver/(?<segment>.*)",
                                "/${segment}","configServerCircuitBreaker"
                                ,"/pdf-editor/fallback/contact-team","lb://CONFIGSERVER")

                )
                .route(buildRoute("/pdf-editor/discoveryserver/**","/pdf-editor/discoveryserver/(?<segment>.*)",
                                "/${segment}","discoveryServerCircuitBreaker"
                                ,"/pdf-editor/fallback/contact-team","lb://DISCOVERYSERVER")

                )

                .build();
    }
    private Function<PredicateSpec, Buildable<Route>> buildRoute(String path, String rewritePath,String replacementPath, String circuitBreakerName, String fallbackUri,
                                                                 String loadBalanceUri){
        return  predicateSpec -> predicateSpec.path(path).
                filters(filterSpec->filterSpec.rewritePath(rewritePath,replacementPath)
                        .circuitBreaker(circuitConfi->
                                circuitConfi.setName(circuitBreakerName)
                                        .setFallbackUri(fallbackUri)
                                        .addStatusCode(HttpStatus.SERVICE_UNAVAILABLE.name())
                        ))
                .uri(loadBalanceUri);
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
