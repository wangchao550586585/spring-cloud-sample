package example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;


//1:配置获取消息的通道接口,Sink
//2:绑定通道
//@EnableBinding(Sink.class)
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
/*    @StreamListener(Sink.INPUT)
    public void input(String message){
        System.out.println(message);
    }*/
}
