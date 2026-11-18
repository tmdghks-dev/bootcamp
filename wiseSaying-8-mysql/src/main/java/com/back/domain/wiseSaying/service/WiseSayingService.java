package com.back.domain.wiseSaying.service;

import com.back.AppConfig;
import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.repository.WiseSayingDbRepository;
import com.back.domain.wiseSaying.repository.WiseSayingRepository;

import com.back.standard.dto.Page;

public class WiseSayingService {
    private final WiseSayingRepository wiseSayingRepository;

    public WiseSayingService() {
        // ⑦ : new WiseSayingFileRepository(AppConfig.getDbDirPath())
        this.wiseSayingRepository = new WiseSayingDbRepository(AppConfig.getSimpleDb(), AppConfig.getDbDirPath());
    }

    public Page<WiseSaying> findForList(String keywordType, String keyword, int page, int pageSize) {
        return wiseSayingRepository.findForList(keywordType, keyword, page, pageSize);
    }

    public void makeSampleDataIfEmpty() {
        if (wiseSayingRepository.count() > 0) return;

        for (int i = 1; i <= 10; i++) {
            write("명언 " + i, "작자미상 " + i);
        }
    }

    public WiseSaying write(String content, String author) {
        WiseSaying wiseSaying = new WiseSaying(content, author);

        wiseSayingRepository.save(wiseSaying);

        return wiseSaying;
    }

    public WiseSaying findById(int id) {
        return wiseSayingRepository.findById(id);
    }

    public void modify(WiseSaying wiseSaying, String content, String author) {
        wiseSaying.setContent(content);
        wiseSaying.setAuthor(author);

        wiseSayingRepository.save(wiseSaying);
    }

    public boolean delete(int id) {
        return wiseSayingRepository.deleteById(id);
    }

    public void build() {
        wiseSayingRepository.build();
    }
}
