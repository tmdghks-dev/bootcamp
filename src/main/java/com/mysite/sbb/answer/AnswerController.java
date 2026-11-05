package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final QuestionService questionService;

    @PostMapping("/create/{id}")
    @Transactional
    public String createAnswer(
            @PathVariable Integer id,
            @RequestParam("content") String content
    ) {
        Question question = questionService.getQuestion(id);

        question.addAnswer(content);

        return String.format("redirect:/question/detail/%s", id);
    }
}
