package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.standard.dto.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class WiseSayingRepository {
    private int lastId = 0;
    private final List<WiseSaying> wiseSayings = new ArrayList<>();

    // 검색(keywordType=content/author) 결과를 최신순으로 정렬한 뒤 page 번째 묶음만 돌려준다.
    public Page<WiseSaying> findForList(String keywordType, String keyword, int page, int pageSize) {
        List<WiseSaying> filtered = wiseSayings.reversed()
                .stream()
                .filter(wiseSaying -> matches(wiseSaying, keywordType, keyword))
                .toList();

        List<WiseSaying> content = filtered.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();

        return new Page<>(content, filtered.size(), page, pageSize);
    }

    private boolean matches(WiseSaying wiseSaying, String keywordType, String keyword) {
        if (keyword.isEmpty()) return true;

        return switch (keywordType) {
            case "author" -> wiseSaying.getAuthor().contains(keyword);
            default -> wiseSaying.getContent().contains(keyword);
        };
    }

    public int count() {
        return wiseSayings.size();
    }

    public void save(WiseSaying wiseSaying) {
        if (wiseSaying.getId() == 0) {
            wiseSaying.setId(++lastId);
            wiseSayings.add(wiseSaying);
        }
    }

    public WiseSaying findById(int id) {
        return wiseSayings
                .stream()
                .filter(wiseSaying -> wiseSaying.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean deleteById(int id) {
        int index = findIndexById(id);

        if (index == -1) return false;

        wiseSayings.remove(index);

        return true;
    }

    private int findIndexById(int id) {
        return IntStream.range(0, wiseSayings.size())
                .filter(i -> wiseSayings.get(i).getId() == id)
                .findFirst()
                .orElse(-1);
    }
}
