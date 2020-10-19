package example.consumer;

import example.channel.MyProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

/**
 * @author WangChao
 * @create 2020/6/15 9:02
 */
@Component
@EnableBinding(MyProcessor.class)
public class MessageListener {
    @StreamListener(MyProcessor.INPUT)
    public void input(String message){
        System.out.println(message);
    }
}
