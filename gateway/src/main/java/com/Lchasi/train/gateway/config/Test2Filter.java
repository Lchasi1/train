package com.Lchasi.train.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class Test2Filter implements GatewayFilter , Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Test2Filter");
        return chain.filter(exchange);//让过滤器往后
//        return exchange.getResponse().setComplete();//让请求结束
    }

    /**
     * 多个过滤器用此来确认顺序，从小到大
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
