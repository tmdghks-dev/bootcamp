package com.ll.ioc;

import com.ll.domain.testPost.testPost.repository.TestPostRepository;
import com.ll.domain.testPost.testPost.service.TestFacadePostService;
import com.ll.domain.testPost.testPost.service.TestPostService;

import java.util.HashMap;
import java.util.Map;

// v1 : 빈 생성 방법을 하드코딩한다. 한 번 만든 빈은 beans 에 넣어 두고 다시 꺼내 쓴다(싱글톤).
public class ApplicationContext {
    private final Map<String, Object> beans = new HashMap<>();

    public ApplicationContext() {

    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        Object bean = beans.get(beanName);

        if (bean == null) {
            bean = createBean(beanName);
            beans.put(beanName, bean);
        }

        return (T) bean;
    }

    // 의존 빈은 genBean 으로 가져와서, 컨테이너에 있는 같은 객체가 주입되게 한다.
    private Object createBean(String beanName) {
        return switch (beanName) {
            case "testPostRepository" -> new TestPostRepository();
            case "testPostService" -> new TestPostService(genBean("testPostRepository"));
            case "testFacadePostService" -> new TestFacadePostService(
                    genBean("testPostService"),
                    genBean("testPostRepository")
            );
            default -> throw new IllegalArgumentException("등록되지 않은 빈 : " + beanName);
        };
    }
}
