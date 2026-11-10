package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Question getQuestion(int id) {
        Optional<Question> opQuestion = questionRepository.findById(id);

        if (opQuestion.isPresent()) {
            return opQuestion.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
}
