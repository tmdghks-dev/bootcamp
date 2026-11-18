package com.ll;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Article {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String title;
    private String body;

    // 컬럼 이름이 isBlind 라서 JSON(Map) 키도 isBlind 로 맞춘다. (lombok getter 는 isBlind())
    @JsonProperty("isBlind")
    private boolean isBlind;
}
