package org.example;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 */
@SpringBootApplication
@EntityScan("org.example.entity")
@EnableFeignClients
public class CartApp {
    /**
     * sentinel支持对RestTemplate的服务调用使用sentinel方法
     * 构造RestTemplate时候,添加@SentinelRestTemplate
     * 资源名
     * httpmethod:scheme://localhost:port/path     协议,主机,端口和路径
     * httpmethod:scheme://localhost:port    协议,主机,端口
     * @SentinelRestTemplate
     * 异常降级
     *      fallback
     *      fallbaclClass
     * 限流熔断
     *      blockHandler
     *      blockHandlerClass
     */
    @LoadBalanced
    @Bean
  /*  @SentinelRestTemplate(fallbackClass = ExceptionUtil.class, fallback = "handleFallback",
            blockHandlerClass = ExceptionUtil.class,
            blockHandler = "handleBlock")*/
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(CartApp.class, args);
    }
}
