package org.example;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author WangChao
 * @create 2020/6/12 23:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(UserImportSelector.class)
//自动找到UserImportSelector,获取加载的配置类
public @interface EnableUserBean {
}
