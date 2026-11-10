package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
public class Answer {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private LocalDateTime createDate;

    @ManyToOne(fetch = LAZY)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String content;
}
