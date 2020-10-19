package example.test;

import example.producer.MessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author WangChao
 * @create 2020/6/15 9:05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProducerTest {
    @Autowired
    private MessageSender messageSender;

    @Test
    public void testSend() {
        for (int i = 0; i < 4; i++) {
            messageSender.send("hello");
        }
    }
}
