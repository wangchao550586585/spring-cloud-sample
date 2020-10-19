# spring-cloud-sample
整合eureka,consul,feign,ribbon,hystrix等
CAP
A:可用性	保持服务可用:多节点
C:一致性	多节点数据一致
P:分区容忍性	是否可以将数据存储多个地方

AC:数据库	多个节点部署服务器
AP:nosql	多个数据存储不同节点时,势必造成数据不一致问题
CP:放弃可用性,常用，数据同步时，服务暂时不可用


架构演变
单机->垂直应用架构->分布式架构->分布式SOA架构->微服务 
 
 

Spring体系架构
 

模拟微服务调用
@SpringBootApplication
@EntityScan("org.example.entity")
public class OrderApp {
    /**
     * 使用spring提供的,发送http请求
     * @return
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public Order findById(@PathVariable String id) {
        User user = restTemplate.getForObject("http://127.0.0.1:9001/user/1", User.class);
        return orderService.findById(id);
    }
}

存在问题
 
注册中心

Eureka不在维护

 
Eureka
http://localhost:8000/

 


配置服务
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        <version>2.2.3.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jsr310</artifactId>
        <version>2.11.0</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.6</version>
    </dependency>

</dependencies>


server:
  port: 9000
eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false  #是否将自己注册到注册中心
    fetch-registry: false           #是否从eureka获取注册信息
    service-url:          #配置暴露给Eureka Client的请求地址
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

@SpringBootApplication
@EnableEurekaServer
public class EurekaApp {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApp.class, args);
    }
}

生产者注册服务
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  <version>2.2.3.RELEASE</version>
</dependency>

<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jsr310</artifactId>
  <version>2.11.0</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
<dependency>
  <groupId>com.google.code.gson</groupId>
  <artifactId>gson</artifactId>
  <version>2.8.6</version>
</dependency>

eureka:
  client:
    service-url:
      defaultZone: http://localhost:9000/eureka/
  instance:
    prefer-ip-address: true     #使用ip地址注册,方便eureka后台查看

@SpringBootApplication
@EntityScan("org.example.entity")
//@EnableEurekaClient
//@EnableDiscoveryClient
//上述2个任意一个均可,新版本支持可不写
public class UserApp {
    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }
}

消费方使用服务
//使用spring提供的,从eureka中获取元数据的工具类
@Autowired
private DiscoveryClient discoveryClient;

@GetMapping("/{id}")
public Order findById(@PathVariable String id) {
    User user = restTemplate.getForObject("http://127.0.0.1:9001/user/1", User.class);
    List<ServiceInstance> instances = discoveryClient.getInstances("service-user");
    ServiceInstance instanceInfo = instances.get(0);

    user = restTemplate.getForObject("http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort() + "/user/1", User.class);


    return orderService.findById(id);
}

Eureka高可用
 

#模拟2个eurekaServer
#端口8000,9000
#2个server需要相互注册到对方服务器
server:
  port: 9000
eureka:
  instance:
    hostname: localhost
  client:
   # register-with-eureka: false  #是否将自己注册到注册中心
   # fetch-registry: false           #是否从eureka获取注册信息
    service-url:          #配置暴露给Eureka Client的请求地址
     # defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
      defaultZone: http://127.0.0.1:8000/eureka/

#模拟2个eurekaServer
#端口8000,9000
#2个server需要相互注册到对方服务器
server:
  port: 8000
eureka:
  instance:
    hostname: localhost
  client:
   # register-with-eureka: false  #是否将自己注册到注册中心
   # fetch-registry: false           #是否从eureka获取注册信息
    service-url:          #配置暴露给Eureka Client的请求地址
     # defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
      defaultZone: http://127.0.0.1:9000/eureka/

 
客户端注册多个使用
eureka:
  client:
    service-url:
      defaultZone: http://localhost:9000/eureka/,http://localhost:8000/eureka/  #注册多个euraka
  instance:
    prefer-ip-address: true     #使用ip地址注册,方便eureka后台查看

存在问题
 
客户端修改配置
eureka:
  instance:
     instance-id:  ${spring.cloud.client.ip-address}:${server.port}

 

服务剔除
多少时间没回复,则默认宕机处理,剔除注册服务
客户端修改配置
eureka:
  instance:
    lease-expiration-duration-in-seconds: 5     #发送心跳间隔,默认30S
    lease-renewal-interval-in-seconds: 10          #设置续约时间,多少秒没心跳,则默认宕机,剔除服务.默认90S


自我保护机制
 
固定范围之内,心跳回复成功率低于多少,就默认不剔除没回复的服务.
服务端修改,生产最好默认

eureka:
  server:
    enable-self-preservation: false #关闭自我保护机制
    eviction-interval-timer-in-ms: 4000   #间隔4秒剔除不可用服务


源码解析加载过程
 



Consul
启动
consul agent -dev -client=0.0.0.0

Dev:开发者模式
Client=0.0.0.0 表示所有机器都可以访问

http://127.0.0.1:8500/ui/dc1/services

注册
    <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-consul-discovery -->
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-consul-discovery</artifactId>
      <version>2.2.3.RELEASE</version>
    </dependency>

<!-- 健康检查-->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
      <version>2.3.1.RELEASE</version>
    </dependency>


spring:
   cloud:
    consul:
      host: 127.0.0.1       #consul服务地址
      port: 8500            #consule服务端口
      discovery:
        register: true      #是否需要注册
        instance-id: ${spring.application.name}-1     #注册的实例ID(全局唯一)
        server-name: ${spring.application.name}       #服务名称
        port: ${server.port}                          #服务请求端口
        prefer-ip-address: true                       #指定开启ip地址注册,而不是DNS
        ip-address: ${spring.cloud.client.ip-address} #当前服务的请求ip

 
调用
Spring对consul封装了,对ribbon支持
 
 

通讯协议
 


集群搭建
 
3台consul server
Consul agent –server –bootstrap-expect 2 –data-dir /etc/consul.d –node=server-3 
-bind=192.0.0.1 –ui –client 0.0.0.0 &
–server 以server身份启动
–bootstrap-expect	集群要求的最少server数量,低于这个数量,集群失效
–data-dir	data存放目录
–node	节点ip
-bind	监听的ip地址,绑定的本机ip地址
–client	客户端的ip地址，0.0.0.0表示不限制
&	后台运行,

每个微服务对应一台client
Consul agent –client=0.0.0.0 –data-dir /etc/consul.d –node=client-1
加入consul server集群,每台consul都需要执行这个命令,leader ip除外
Consul join leader的ip地址

查看集群
Consul members


 

存在问题
 


spring-Boot自动装载外部配置
 

注解
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(UserImportSelector.class)
//自动找到UserImportSelector,获取加载的配置类
public @interface EnableUserBean {
}

设置配置类名称
public class UserImportSelector  implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        //设置配置类的名称,他就去加载
        return new String[]{UserConfiguration.class.getName()};
    }
}

配置类
//没有配置注解
public class UserConfiguration {
    @Bean
    public User getUser(){
        User user = new User();
        user.setId(10L);
        return user;
    }
}

使用注解获取外部配置对象
*/
@EnableUserBean
public class App {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext spring = new AnnotationConfigApplicationContext(App.class);
        User bean = spring.getBean(User.class);
        System.out.println(bean );
    }
}



服务调用
Feign
不在维护
1.	导入依赖

<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
  <version>2.2.3.RELEASE</version>
</dependency>

2.	配置调用接口

//name:服务提供者名
@FeignClient("service-user")
public interface UserFeignClient {
    @GetMapping("/user/{id}")
    public User findById(@PathVariable("id") Long id) ;
}

3.	启动类上开启feign

@SpringBootApplication
@EntityScan("org.example.entity")
@EnableFeignClients
public class OrderApp {
    /**
     * 使用spring提供的,发送http请求
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}

4.	通过接口调用远程微服务



@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;
    //使用spring提供的,从eureka中获取元数据的工具类
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private UserFeignClient userFeignClient;


    @GetMapping("/{id}")
    public User findById(@PathVariable String id) {
        //1:基于http调用
        // User user = restTemplate.getForObject("http://127.0.0.1:9001/user/1", User.class);

        //2:基于元数据调用
       // List<ServiceInstance> instances = discoveryClient.getInstances("service-user");
        //ServiceInstance instanceInfo = instances.get(0);
        //  User  user = restTemplate.getForObject("http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort() + "/user/1", User.class);

        //3:使用ribbon调用,根据服务名获取
        //User user = restTemplate.getForObject("http://service-user/user/1", User.class);
        //Order byId = orderService.findById(id);

        //feign调用
        User user = userFeignClient.findById(1L);
        return user;
    }}

自定义feign

 




负载均衡
 
数据压缩
 
日志支持
#配置feign日志输出
#日志级别,NONE:不输出日志,   BASIC:适用于生产环境追踪
#HEADERS:BASIC基础上记录请求头和响应头信息  FULL:记录所有
feign:
  client:
    config:
      service-user:           #输出那个服务的日志
        loggerLevel: FULL
#针对某个类的日志级别
logging:
  level:
    org.example.feign.UserFeignClient: debug

源码解析
 

Ribbon
 
服务调用
	Eureka集成了ribbon
	创建restTemplate,声明@localBalanced
 
	使用restTemplate,直接通过服务名调用
 
负载均衡
	客户端负载均衡,会获取所有服务地址,当存在多个相同微服务提供者,根据负载均衡算法,选择调用地址
 
客户端修改策略
#ribbon调用注册服务方负载均衡策略   服务名-     ribbon     
NFLoadBalancerRuleClassName
service-order:
    ribbon:
      NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule



 
 
重试机制
当有一个服务宕机后,访问会出异常,这个时候引入重试机制,在超过请求超时连接依旧没连接服务,则连接下一个可用的服务重试.
导入maven
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
    <version>1.3.0</version>
    <exclusions>
        <exclusion>
                <groupId>org.springframework</groupId>
                <artifactId>spring-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>

设置重试时机

  #ribbon调用注册服务方负载均衡策略   服务名-     ribbon     NFLoadBalancerRuleClassName
service-order:
    ribbon:
      ConnectTimeout: 250       #ribbon连接超时时间
      ReadTimeout: 1000         #ribbon数据读取时间
      OkToRetryOnAllOperations: true    #是否对所有操作都进行重试
      MaxAutoRetriesNextServer: 1     #切换实例的重试次数
      MaxAutoRetries: 1             #对当前实例的重试次数


源码解析
 
服务隔离
当多个线程访问同一个服务A方法,会造成B方法请求延迟.可以给AB方法单独创建线程池分配线程处理.也可以给A方法设置信号量,只允许多少线程访问,超过值异常,这样不会影响到其他方法调用
 
线程池隔离
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

调用
@GetMapping("/{id}")
public User findById(@PathVariable String id) {
    //1:基于http调用
    // User user = restTemplate.getForObject("http://127.0.0.1:9001/user/1", User.class);

    //2:基于元数据调用
   // List<ServiceInstance> instances = discoveryClient.getInstances("service-user");
    //ServiceInstance instanceInfo = instances.get(0);
    //  User  user = restTemplate.getForObject("http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort() + "/user/1", User.class);

    //3:使用ribbon调用,根据服务名获取
    //User user = restTemplate.getForObject("http://service-user/user/1", User.class);
    //Order byId = orderService.findById(id);

    //4feign调用
    //User user = userFeignClient.findById(1L);

    User user = new OrderCommand(restTemplate,1L).execute();
    return user;
}


Hystrix
雪崩效应
 
 
服务隔离
 
熔断降级
 
 
服务限流
 


对RestTemplate支持
1.	导入依赖

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>2.2.3.RELEASE</version>
</dependency>

2.	启动类激活

//激活hystrix
@EnableCircuitBreaker
public class OrderApp {
    /**
     * 使用spring提供的,发送http请求
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}

3.	配置熔断触发降级逻辑

/**
 * 降级方法
 *  和需要受到保护的方法返回值,参数一致
 *
 * @param id
 * @return
 */
public User orderFallBack(String id) {
    User user = new User();
    return user;
}

4.	在需要保护的接口上配置@HystrixCommand配置

//配置熔断保护降级方法
@HystrixCommand(fallbackMethod = "orderFallBack")
@GetMapping("/{id}")
public User findById(@PathVariable String id) throws InterruptedException {
    //1:基于http调用
    // User user = restTemplate.getForObject("http://127.0.0.1:9001/user/1", User.class);

    //2:基于元数据调用
   // List<ServiceInstance> instances = discoveryClient.getInstances("service-user");
    //ServiceInstance instanceInfo = instances.get(0);
    //  User  user = restTemplate.getForObject("http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort() + "/user/1", User.class);

    //3:使用ribbon调用,根据服务名获取
    Thread.sleep(1000);
    User user = restTemplate.getForObject("http://service-user/user/1", User.class);
    //Order byId = orderService.findById(id);

    //4:feign调用
    //User user = userFeignClient.findById(1L);

    //5:自定义熔断,以及线程池隔离调用
    //User user = new OrderCommand(restTemplate,1L).execute();

    return user;
}

超时降级
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000    #设置连接超时时间,超过则降级


使用统一降级方法
 
 
 

对Feign支持
1.	引入依赖,Feign已经集成
Feign中配置开启hystrix,所以启动类不需要@EnableHystrix
 
2.	自定义接口实现类,实现熔断触发的降级逻辑
 

3.	修改FeignClient接口添加降级方法的支持
 

控制台监控信息
http://localhost:9002/actuator/hystrix.stream
http://localhost: 9002/hystrix

1.	导入依赖
 
2.	Boot开启hystrix

 
3.	暴露访问接口
 
 
4.	访问控制台
 
http://localhost:9002/hystrix
 
 
 

聚合监控

http://localhost:8031/turbine.stream
http://localhost:8031/hystrix
 
 
 
 
 

断路器的状态
Hystrix可以对请求失败,拒绝,超时的请求统一降级处理
 

 

设置熔断比率
 


熔断器隔离策略

 
 
执行过程

 
Sentinel

 

下载启动

下载地址:https://github.com/alibaba/Sentinel/releases
java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard.jar

用户名:密码			sentinel: sentinel
访问地址			http://localhost:8080/

所有服务交由控制台管理
1.	导入依赖
 

2.	配置绑定服务地址
 


 

通用资源保护
注册资源和降级规则
 
 
查看

 

加载本地配置的
 
 


对RestTemplate的资源保护
 

导入依赖
 

 
 

对Feign支持
 
导入依赖
 
 
 
 
 

微服务网关
 
 
Zuul

 
启动服务
1.	导入依赖
 
2.	开启Zuul
 
3.	配置端口
 
路由
根据请求的url的不同匹配规则,将不同请求转发到对应的微服务中处理
基础路由配置
 
 
 
面向服务的路由配置(基于eureka)
1.	添加eureka依赖

<!--注册中心 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>

2.	开启eureka的客户端服务发现

@SpringBootApplication
//开启Zuul网关
@EnableZuulProxy
//eureka的服务发现,可不用
//@EnableDiscoveryClient
public class ZuulApp {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApp.class, args);
    }
}

3.	Zuul配置eureka

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8000/eureka/
  instance:
    prefer-ip-address: true

4.	修改路由中映射配置
 

简化路由配置
 

过滤器

 

 
 

自定义过滤器
package example.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author WangChao
 * @create 2020/6/14 13:29
 */
@Component
public class LoginFilter extends ZuulFilter {
    /**
     * 定义过滤器类型
     * pre          访问前
     * routing      路由映射规则
     * post         返回微服务结果后处理
     * error        对返回异常处理
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 指定过滤器执行顺序,值越小优先级越高
     * @return
     */
    @Override
    public int filterOrder() {
        return 1;
    }

    /**
     * 当前过滤器是否生效
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器具体执行逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String parameter = request.getParameter("access-token");
        if (parameter==null){
            //拦截请求
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }

}



存在问题
同步阻塞访问
 
不支持websocket

 

源码解析
 


SpringCloud Gateway
概念
 

路由配置
1.	导入依赖

<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
  </dependency>
</dependencies>

2.	配置启动类


@SpringBootApplication
public class GateWayApp {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApp.class, args);
    }
}

3.	配置文件

server:
  port: 8080
spring:
  application:
    name: service-gateway
#配置gateWay路由
  cloud:
    gateway:
      routes:
         #配置路由,路由id,路由到微服务的url,断言(判断条件)
        - id: service-order
          uri: http://127.0.0.1:9002      #目标微服务请求地址
          predicates:                       #断言,路由条件,将/order/** 拦截, uti+Path访问
            - Path=/order/**

路由规则
 
 
 
动态路由（面向服务调用）
 
 
路径重写
 
开启根据微服务名称转发
 

过滤器
 
 
 
全局过滤器
 

统一鉴权
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


网关限流
限流算法
 

 
 
基于filter的限流
1.	准备redis,并导入依赖

<!-- redis !-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

2.	修改yml配置

server:
  port: 8080
spring:
  application:
    name: service-gateway
  redis:
    host: localhost
    port: 6379
    password: root
    database: 0
  #配置gateWay路由
  cloud:
    gateway:
      routes:
         #配置路由,路由id,路由到微服务的url,断言(判断条件)
        - id: service-order
          #uri: http://127.0.0.1:9002      #目标微服务请求地址
          uri: lb://service-order          #根据微服务名称从注册中心拉取服务请求路径
          predicates:                       #断言,路由条件,将/order/** 拦截, uti+Path访问
            #- Path=/order/**
            - Path=/pre-order/**            #使用重写过滤器将当前路径转发到 http://127.0.0.1:9002/order/**
          #配置路由过滤器
          filters:        #yml中$需要写成$\      访问路径http://localhost:8080/pre-order/order/9
            - RewritePath=/pre-order/(?<segment>.*),/$\{segment}   #路径重写过滤器,将1和2级路径重写为2级路径
            - name: RequestRateLimiter
              args:
                  #使用SpEL从容器中获取对象
                key-resolver: '#{@pathKeyResolver}'
                  #令牌桶每秒填充平均速率
                redis-rate-limiter.replenishRate: 1
                  #令牌桶上限
                redis-rate-limiter.burstCapacity: 3

      #配置自动根据微服务名称进行路由转发    http://localhost:8080/service-order/order/9
      discovery:
        locator:
          enabled: true   #开启根据服务名称自动转发
          lower-case-service-id: true   #微服务名小写呈现

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8000/eureka/
  instance:
    prefer-ip-address: true

 

3.	配置redis中key的解析器
4.	package org.example;

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




基于sentinel的限流
 
 
 

<!-- 使用sentinel限流-->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-spring-cloud-gateway-adapter</artifactId>
</dependency>


/**
 * sentinel限流配置
 *
 * @author WangChao
 * @create 2020/6/14 18:49
 */
@Configuration
public class GatewayConfiguration {
    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(List<ViewResolver> viewResolvers, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolvers;
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 配置限流的异常处理器
     *
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * 配置限流过滤器
     *
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * 配置初始化的限流参数
     */
    @PostConstruct
    public void initRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        //指定资源限流规则
        rules.add(new GatewayFlowRule
                //路由id
                ("service-order")
                //限流阈值
                .setCount(1)
                //统计时间
                .setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }}


自定义异常处理器
 
    /**
     * 自定义限流处理器
     * 针对异常,返回值处理
     */
    @PostConstruct
    public void initBlockRequestHandler(){
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            HashMap<String, String> result = new HashMap<>();
            result.put("code","0001");
            result.put("message","限流了");
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(result));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}

自定义API限流分组
/**
 * 自定义API限流分组
 */
@PostConstruct
private void initCustomizedApis() {
    Set<ApiDefinition> definitions = new HashSet<>();
    ApiDefinition apiDefinition = new ApiDefinition("service-order222")
            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                //pre-order/**  针对这个链接指定规则
                add(new ApiPathPredicateItem().setPattern("/pre-order/**")
                        .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
            }});

    definitions.add(apiDefinition);
    GatewayApiDefinitionManager.loadApiDefinitions(definitions);
}


 




网关高可用

 


采用nginx配置即可

链路追踪
 

 

sleuth入门
 

所有需要链路追踪的项目添加依赖

<!--链路追踪-->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>

添加log日志
logging:
  level:
    root: info
    org.springframework.web.servlet.DispatcherServlet: DEBUG
    org.springframework.cloud.sleuth: DEBUG


 
 
 

Zipkin
 
 
 
启动web服务端
镜像下载
https://maven.aliyun.com/mvn/search
启动
java -jar zipkin-server-2.21.4-exec.jar
访问
http://localhost:9411
客户端注册
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>

server:
  port: 8080
spring:
  zipkin:
    base-url: http://localhost:9411/      #server的请求地址
    sender:
      type: web     #数据的传输方式,以Http的形式向server端发送数据
  sleuth:
    sampler:
      probability: 1   #采样比   收集所有的数据


 
持久化数据库
获取脚本
https://github.com/openzipkin/zipkin/blob/master/zipkin-storage/mysql-v1/src/main/resources/mysql.sql
启动配置数据库参数
java -jar zipkin-server-2.21.4-exec.jar --STORAGE_TYPE=mysql --MYSQL_HOST=127.0.0.1  --MYSQL_TCP_PORT=3306 --MYSQL_U
SER=root --MYSQL_PASS=root --MYSQL_DB=zipkin

异步存储获取数据
java -jar zipkin-server-2.21.4-exec.jar  --RABBIT_ADDRESSES=127.0.0.1:5672

<!--消息异步发送rabbitmq-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit</artifactId>
</dependency>

#zipkin使用rabbitmq采集数据
zipkin:
  #base-url: http://localhost:9411/      #server的请求地址
  sender:
    #type: web     #数据的传输方式,以Http的形式向server端发送数据
    type: rabbit
sleuth:
  sampler:
    probability: 1   #采样比   收集所有的数据
rabbitmq:
  host: localhost
  port: 5672
  username: guest
  password: guest
  listener:   #配置重试策略
    direct:
      retry:
        enabled: true
    simple:
      retry:
        enabled: true


Spring cloud stream
 
 
 
定义生产者
<!--spring-cloud-stream支持-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream</artifactId>
    <version>3.0.5.RELEASE</version>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    <version>3.0.5.RELEASE</version>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
    <version>3.0.5.RELEASE</version>
</dependency>

 
server:
  port: 7002
spring:
  application:
    name: stream-comsumer
  rabbitmq:
    addresses: 127.0.0.1
    username: guest
    password: guest
  cloud:
    stream:
      bindings:
        output:
          destination: default
      binders:    #配置绑定器
        defaultRabbit:
          type: rabbit

/**
 * @EnableBinding 绑定对应通道
 * MessageChannel由绑定的内置接口Source,获取MessageChannel,也就是添加@EnableBinding(Source.class)注解
 */
@EnableBinding(Source.class)
@SpringBootApplication
public class ProducerApp implements CommandLineRunner {
    @Autowired
    private MessageChannel output;

    @Override
    public void run(String... args) throws Exception {
        //发送消息
        output.send(MessageBuilder.withPayload("hello").build());
    }

    public static void main(String[] args) {
        SpringApplication.run(ProducerApp.class, args);
    }
}

 
消费者
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>


server:
  port: 7001
spring:
  application:
    name: stream-comsumer
  rabbitmq:
    addresses: 127.0.0.1
    username: guest
    password: guest
  cloud:
    stream:
      bindings:
        input:    #内置获取消息的通道,从default中获取消息
          destination: default    #指定消息发送的目的地,发送到default的exchange
      binders:    #配置绑定器
          defaultRabbit:
            type: rabbit


//1:配置获取消息的通道接口,Sink
//2:绑定通道
@EnableBinding(Sink.class)
@SpringBootApplication
public class ComsumerApp  {


    public static void main(String[] args) {
        SpringApplication.run(ComsumerApp.class, args);
    }

    /**
     * 3:配置监听方法
     * 监听binding中的消息
     * @param message
     */
    @StreamListener(Sink.INPUT)
    public void input(String message){
        System.out.println(message);
    }
}

代码优化
@Component
@EnableBinding(Source.class)
public class MessageSender {
    @Autowired
    private MessageChannel output;

    public void send(Object object) {
        //发送消息
        output.send(MessageBuilder.withPayload("hello").build());
    }
}

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProducerTest {
    @Autowired
    private MessageSender messageSender;
    @Test
    public void testSend(){
        messageSender.send("hello");
    }
}

@Component
@EnableBinding(Sink.class)
public class MessageListener {
    @StreamListener(Sink.INPUT)
    public void input(String message){
        System.out.println(message);
    }
}

自定义消息通道
public interface MyProcessor {
    /**
     * 消息生产者的配置
     */
    String OUTPUT = "myoutput";

    @Output("myoutput")
    MessageChannel myoutput();

    /**
     * 消息消费者的配置
     */
    String INPUT = "myinput";

    @Input("myinput")
    SubscribableChannel myinput();
}

@Component
@EnableBinding(MyProcessor.class)
//@EnableBinding(Source.class)
public class MessageSender {
    @Autowired
    @Qualifier("myoutput")
    private MessageChannel output;

    public void send(Object object) {
        //发送消息
        output.send(MessageBuilder.withPayload("hello").build());
    }
}
 

@Component
@EnableBinding(MyProcessor.class)
//@EnableBinding(Sink.class)
public class MessageListener {
    @StreamListener(MyProcessor.INPUT)
    //@StreamListener(Sink.INPUT)
    public void input(String message){
        System.out.println(message);
    }
}
 

消息分组
 
 
在同一个group中的多个消费者,只有一个可以获取到消息

 
消息分区
生产者配置
 
消费者配置
 
 
配置中心
 
 
 
 
开启server
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>


server:
  port: 9200
spring:
  application:
    name: config-user
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/nuanfeng_wangchao/config2.git


package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;


@SpringBootApplication
@EnableConfigServer
public class ConfigApp {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApp.class, args);
    }
}

 
客户端开启
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

spring:
  cloud:
    config:
      name: application     #应用名称,对应git配置文件名称前半部分
      profile: dev          #开发环境
      label: master         #git分支
      uri: http://localhost:9200      #config-server请求地址

动态刷新配置文件
客户端添加依赖
<!-- 健康检查-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

#暴露一个动态刷新的断点,访问后动态刷新
management:
  endpoints:
    web:
      exposure:
        include: refresh
 
当更新配置文件时,post访问http://localhost:9001/actuator/refresh,就会动态刷新配置
Refresh是在如下配置的
 

高可用
 
配置服务端

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- 健康检查-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>


<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-bus</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>


server:
  port: 9201
spring:
  application:
    name: config-user
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/nuanfeng_wangchao/config2.git
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8000/eureka/
  instance:
    prefer-ip-address: true
    instance-id:  ${spring.cloud.client.ip-address}:${server.port}

配置客户端
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

 

消息总线
 
 
服务端
<!--消息总线依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-bus</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
</dependency>

 
客户端
导入依赖
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- 健康检查-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!--消息总线依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-bus</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
</dependency>

配置文件删除,只需要服务端暴露就行了
#暴露一个动态刷新的断点,访问后动态刷新
management:
  endpoints:
    web:
      exposure:
        include: bus-refresh

Git服务器上配置文件添加
rabbitmq:
  addresses: 127.0.0.1
  username: guest
  password: guest


当更新配置文件时,post访问服务端暴露的端点, http://localhost: 9200/actuator/ bus-refresh
,就会动态刷新所有客户端配置文件

