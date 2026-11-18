package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.standard.dto.Page;

import java.util.List;

// 저장 방식(메모리 → 파일 → DB)이 바뀌어도 서비스는 이 인터페이스만 본다.
public interface WiseSayingRepository {
    void save(WiseSaying wiseSaying);

    WiseSaying findById(int id);

    boolean deleteById(int id);

    // 최신순(번호 내림차순) 전체
    List<WiseSaying> findAll();

    // 모든 명언을 data.json 으로 내보낸다.
    void build();

    default int count() {
        return findAll().size();
    }

    // 검색(keywordType=content/author) 결과를 최신순으로 정렬한 뒤 page 번째 묶음만 돌려준다.
    default Page<WiseSaying> findForList(String keywordType, String keyword, int page, int pageSize) {
        List<WiseSaying> filtered = findAll()
                .stream()
                .filter(wiseSaying -> matches(wiseSaying, keywordType, keyword))
                .toList();

        List<WiseSaying> content = filtered.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();

        return new Page<>(content, filtered.size(), page, pageSize);
    }

    private static boolean matches(WiseSaying wiseSaying, String keywordType, String keyword) {
        if (keyword.isEmpty()) return true;

        return switch (keywordType) {
            case "author" -> wiseSaying.getAuthor().contains(keyword);
            default -> wiseSaying.getContent().contains(keyword);
        };
    }
}
