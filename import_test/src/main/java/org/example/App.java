package org.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 */
@EnableUserBean
public class App {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext spring = new AnnotationConfigApplicationContext(App.class);
        User bean = spring.getBean(User.class);
        System.out.println(bean );
    }
}
