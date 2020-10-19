package org.example;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.function.Predicate;

/**
 * @author WangChao
 * @create 2020/6/12 23:21
 */
public class UserImportSelector  implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        //设置配置类的名称,他就去加载
        return new String[]{UserConfiguration.class.getName()};
    }
}
