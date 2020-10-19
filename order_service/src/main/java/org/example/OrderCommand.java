package org.example;

import com.netflix.hystrix.*;
import org.example.entity.User;
import org.springframework.web.client.RestTemplate;

/**
 * @author WangChao
 * @create 2020/6/13 12:36
 */
public class OrderCommand extends HystrixCommand<User> {
    private RestTemplate restTemplate;
    private Long id;

    public OrderCommand(RestTemplate restTemplate, Long id) {
        super(setter());
        this.restTemplate = restTemplate;
        this.id = id;
    }

    private static Setter setter() {
        //服务分组
        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("order_user");
        //服务标识
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("user");
        //线程池名称
        HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("order_user_pool");

        /*
         * 线程池配置
         */
        HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties.Setter()
                //线程池大小为50
                .withCoreSize(50)
                //线程存活时间15秒
                .withKeepAliveTimeMinutes(15)
                //队列等待阈值为100,超过100拒绝
                .withQueueSizeRejectionThreshold(100);
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
                //线程池方式隔离
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
        return Setter.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
                .andThreadPoolPropertiesDefaults(threadPoolProperties)
                .andCommandPropertiesDefaults(commandProperties);
    }

    @Override
    protected User run() throws Exception {
        return restTemplate.getForObject("http://service-user/user/"+id,User.class);
    }

    /**
     * 降级方法,当超过访问上限,返回对象
     * @return
     */
    @Override
    protected User getFallback() {
        return super.getFallback();
    }
}
