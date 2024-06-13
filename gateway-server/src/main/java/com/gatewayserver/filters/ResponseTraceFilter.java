package com.gatewayserver.filters;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ResponseTraceFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);
    private  final  FilterUtility filterUtility;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(()->{
                    var request= exchange.getRequest();
                    var traceId= filterUtility.getTraceId(request);
                    exchange.getResponse().getHeaders().add(filterUtility.TRACE_ID,traceId);
                    logger.info("Added trace-Id: {} at response header: {}",traceId,exchange.getResponse().getHeaders());
                }
        ));
    }
}
