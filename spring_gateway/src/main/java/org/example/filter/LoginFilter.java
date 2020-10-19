package org.example.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器
 * @author WangChao
 * @create 2020/6/14 17:37
 */
@Component
public class LoginFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("执行全局过滤器");
        String first = exchange.getRequest().getQueryParams().getFirst("access-token");
        if (first==null){
            System.out.println("没登录");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            //结束请求
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    /**
     * 执行顺序优先级
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
