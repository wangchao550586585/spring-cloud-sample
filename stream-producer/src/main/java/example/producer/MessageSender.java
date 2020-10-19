package example.producer;

import example.channel.MyProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author WangChao
 * @create 2020/6/15 8:59
 */
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
