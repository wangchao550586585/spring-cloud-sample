package example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
/**
 * @EnableBinding 绑定对应通道
 * MessageChannel由绑定的内置接口Source,获取MessageChannel,也就是添加@EnableBinding(Source.class)注解
 */
//@EnableBinding(Source.class)
@SpringBootApplication
public class ProducerApp
        //implements CommandLineRunner
{
 /*   @Autowired
    private MessageChannel output;

    @Override
    public void run(String... args) throws Exception {
        //发送消息
        output.send(MessageBuilder.withPayload("hello").build());
    }*/

    public static void main(String[] args) {
        SpringApplication.run(ProducerApp.class, args);
    }
}
