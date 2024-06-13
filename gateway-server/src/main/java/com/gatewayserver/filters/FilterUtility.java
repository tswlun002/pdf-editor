package com.gatewayserver.filters;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtility {
    public  final String TRACE_ID ="trace-Id" ;

    public String getTraceId(ServerHttpRequest request) {
        return request.getHeaders().getFirst(TRACE_ID) ;
    }

    public void setTraceId(ServerWebExchange exchange,String key, String value) {
        exchange.mutate().request(exchange.getRequest().mutate().header(key, value).build()).build();
    }
}
