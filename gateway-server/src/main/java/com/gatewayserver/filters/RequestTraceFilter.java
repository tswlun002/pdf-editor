package com.gatewayserver.filters;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
@AllArgsConstructor
public class RequestTraceFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);
    private final FilterUtility filterUtility;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        var traceId = filterUtility.getTraceId(request);
        if(traceId==null){traceId = generateTraceId();
            filterUtility.setTraceId(exchange, filterUtility.TRACE_ID, traceId);
            logger.info("Set trace-Id: {}", traceId);
        }
        else {
            logger.info("Get  trace-Id: {}", filterUtility.getTraceId(request));
        }
        return chain.filter(exchange);
    }
    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }
}
