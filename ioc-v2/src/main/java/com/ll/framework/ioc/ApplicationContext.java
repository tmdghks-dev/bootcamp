package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import com.ll.standard.util.Ut;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// v2 : basePackage 를 스캔해서 @Component(@Service, @Repository 포함) 클래스를 빈으로 만든다.
public class ApplicationContext {
    private final String basePackage;
    // 빈 이름 -> 클래스 (빈 생성 정보)
    private final Map<String, Class<?>> beanDefinitions = new HashMap<>();
    // 빈 이름 -> 만들어진 객체 (싱글톤)
    private final Map<String, Object> beans = new HashMap<>();

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        // @Service, @Repository 는 @Component 가 붙은 어노테이션이라 함께 잡힌다. 어노테이션 자체는 뺀다.
        new Reflections(basePackage)
                .getTypesAnnotatedWith(Component.class)
                .stream()
                .filter(cls -> !cls.isAnnotation() && !cls.isInterface())
                .forEach(cls -> beanDefinitions.put(Ut.str.lcfirst(cls.getSimpleName()), cls));
    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        Object bean = beans.get(beanName);

        if (bean == null) {
            Class<?> cls = beanDefinitions.get(beanName);

            if (cls == null) throw new IllegalArgumentException("등록되지 않은 빈 : " + beanName);

            bean = createBean(cls);
            beans.put(beanName, bean);
        }

        return (T) bean;
    }

    // 생성자 파라미터 타입으로 의존 빈 이름을 알아내 먼저 만들고(genBean), 그 빈을 넣어 생성한다.
    private Object createBean(Class<?> cls) {
        Constructor<?> constructor = Arrays.stream(cls.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow();

        Object[] args = Arrays.stream(constructor.getParameterTypes())
                .map(paramType -> genBean(Ut.str.lcfirst(paramType.getSimpleName())))
                .toArray();

        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("빈 생성 실패 : " + cls.getName(), e);
        }
    }
}
