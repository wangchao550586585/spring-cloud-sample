package org.example;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import org.example.entity.Item;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

/**
 * @author WangChao
 * @create 2020/6/14 10:26
 */
public class ExceptionUtil {
    //异常降级处理
    public static SentinelClientHttpResponse handleFallback(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        Item item = new Item();
        item.setBarcode("异常熔断降级");
        return new SentinelClientHttpResponse(JSON.toJSONString(item));
    }

    //限流熔断降级处理
    public static SentinelClientHttpResponse handleBlock(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        Item item = new Item();
        item.setBarcode("限流熔断降级");
        return new SentinelClientHttpResponse(JSON.toJSONString(item));
    }
}
