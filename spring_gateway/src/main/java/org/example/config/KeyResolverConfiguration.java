package org.example.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author WangChao
 * @create 2020/6/14 18:19
 */
@Configuration
public class KeyResolverConfiguration {
    /**
     * 编写基于请求路径的限流规则
     * 基于路径        比如abc路径,根据配置规则,往令牌桶放入令牌个数,每次只有令牌个数允许访问abc
     * 基于请求地址 127.0.0.1
     * 基于参数
     *
     * @return
     */

    @Bean
    public KeyResolver pathKeyResolver() {
/*        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                return Mono.just(exchange.getRequest().getPath().toString());
            }
        };*/

        return exchange -> Mono.just(
                //基于请求参数限流
                exchange.getRequest().getQueryParams().getFirst("userId")
                //基于ip限流
                //exchange.getRequest().getHeaders().getFirst("X-Forwarded-For")
        );
    }

}
