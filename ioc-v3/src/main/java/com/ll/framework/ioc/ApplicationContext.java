package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Bean;
import com.ll.framework.ioc.annotations.Component;
import com.ll.framework.ioc.annotations.Configuration;
import com.ll.standard.util.Ut;
import org.reflections.Reflections;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// v3 : 컴포넌트 스캔(v2) + @Configuration 클래스의 @Bean 메서드로 만드는 빈
public class ApplicationContext {
    private final String basePackage;
    // 빈 이름 -> 빈 생성 정보 (타입 + 만드는 방법)
    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
    // 빈 이름 -> 만들어진 객체 (싱글톤)
    private final Map<String, Object> beans = new HashMap<>();

    // creator : 생성자 또는 @Bean 메서드. configBeanName 은 @Bean 메서드를 가진 설정 빈 이름 (생성자면 null)
    private record BeanDefinition(Class<?> type, Executable creator, String configBeanName) {
    }

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        Reflections reflections = new Reflections(basePackage);

        // 1. @Component(@Service, @Repository, @Configuration 포함) 클래스 -> 클래스 이름(첫 글자 소문자)으로 등록
        reflections.getTypesAnnotatedWith(Component.class)
                .stream()
                .filter(cls -> !cls.isAnnotation() && !cls.isInterface())
                .forEach(cls -> beanDefinitions.put(
                        Ut.str.lcfirst(cls.getSimpleName()),
                        new BeanDefinition(cls, mainConstructor(cls), null)
                ));

        // 2. @Configuration 클래스의 @Bean 메서드 -> 메서드 이름으로 등록
        reflections.getTypesAnnotatedWith(Configuration.class)
                .stream()
                .filter(cls -> !cls.isAnnotation())
                .forEach(configClass -> Arrays.stream(configClass.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Bean.class))
                        .forEach(method -> beanDefinitions.put(
                                method.getName(),
                                new BeanDefinition(method.getReturnType(), method, Ut.str.lcfirst(configClass.getSimpleName()))
                        )));
    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        Object bean = beans.get(beanName);

        if (bean == null) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);

            if (beanDefinition == null) throw new IllegalArgumentException("등록되지 않은 빈 : " + beanName);

            bean = createBean(beanDefinition);
            beans.put(beanName, bean);
        }

        return (T) bean;
    }

    // 파라미터에 필요한 빈을 먼저 만들고(genBean), 생성자 또는 @Bean 메서드를 호출한다.
    private Object createBean(BeanDefinition beanDefinition) {
        Executable creator = beanDefinition.creator();

        Object[] args = Arrays.stream(creator.getParameters())
                .map(parameter -> genBean(findBeanName(parameter)))
                .toArray();

        try {
            creator.setAccessible(true);

            if (creator instanceof Method method) {
                Object configBean = genBean(beanDefinition.configBeanName());
                return method.invoke(configBean, args);
            }

            return ((Constructor<?>) creator).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("빈 생성 실패 : " + beanDefinition, e);
        }
    }

    // 파라미터 타입에 맞는 빈 이름을 찾는다. 같은 타입이 여럿이면 파라미터 이름이 같은 빈을 고른다.
    private String findBeanName(Parameter parameter) {
        var candidates = beanDefinitions.entrySet()
                .stream()
                .filter(entry -> parameter.getType().isAssignableFrom(entry.getValue().type()))
                .map(Map.Entry::getKey)
                .toList();

        if (candidates.size() == 1) return candidates.getFirst();

        if (parameter.isNamePresent() && candidates.contains(parameter.getName())) return parameter.getName();

        throw new IllegalStateException("주입할 빈을 하나로 정할 수 없음 : " + parameter + " 후보 " + candidates);
    }

    private static Constructor<?> mainConstructor(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow();
    }
}
