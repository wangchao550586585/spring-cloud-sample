package example.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author WangChao
 * @create 2020/6/15 9:12
 */
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
