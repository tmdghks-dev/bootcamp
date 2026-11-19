package com.back.domain.member.member.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity                 // 이 클래스는 DB 테이블(member)과 연결된다
@Getter                 // getUsername() 같은 getter를 롬복이 자동 생성
@NoArgsConstructor      // JPA가 객체를 만들 때 필요한 빈 생성자
public class Member extends BaseEntity {   // id, createDate, modifyDate는 BaseEntity가 줌
    @Column(unique = true)   // username은 중복 불가
    private String username;
    private String password;
    private String name;

    public Member(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}
