package com.back.domain.wiseSaying.controller;

import com.back.Rq;
import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.service.WiseSayingService;

import com.back.standard.dto.Page;

import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WiseSayingController {
    private final Scanner scanner;
    private final WiseSayingService wiseSayingService;

    public WiseSayingController(Scanner scanner) {
        this.scanner = scanner;
        this.wiseSayingService = new WiseSayingService();
    }

    public void makeSampleDataIfEmpty() {
        wiseSayingService.makeSampleDataIfEmpty();
    }

    public void actionList(Rq rq) {
        String keywordType = rq.getParam("keywordType", "");
        String keyword = rq.getParam("keyword", "");
        int page = rq.getParamAsInt("page", 1);

        if (!keyword.isEmpty()) {
            System.out.println("----------------------");
            System.out.println("검색타입 : " + keywordType);
            System.out.println("검색어 : " + keyword);
            System.out.println("----------------------");
        }

        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");

        Page<WiseSaying> wiseSayingPage = wiseSayingService.findForList(keywordType, keyword, page, 5);

        for (WiseSaying wiseSaying : wiseSayingPage.content()) {
            System.out.printf("%d / %s / %s\n", wiseSaying.getId(), wiseSaying.getAuthor(), wiseSaying.getContent());
        }

        System.out.println("----------------------");
        System.out.println("페이지 : " + pageMenu(wiseSayingPage));
    }

    // 예) 1 / [2] / 3
    private String pageMenu(Page<WiseSaying> wiseSayingPage) {
        return IntStream.rangeClosed(1, wiseSayingPage.totalPages())
                .mapToObj(i -> i == wiseSayingPage.page() ? "[" + i + "]" : String.valueOf(i))
                .collect(Collectors.joining(" / "));
    }

    public void actionWrite() {
        System.out.print("명언 : ");
        String content = scanner.nextLine().trim();
        System.out.print("작가 : ");
        String author = scanner.nextLine().trim();

        WiseSaying wiseSaying = wiseSayingService.write(content, author);

        System.out.println("%d번 명언이 등록되었습니다.".formatted(wiseSaying.getId()));
    }

    public void actionDelete(Rq rq) {
        int id = rq.getParamAsInt("id", -1);

        if (id == -1) {
            System.out.println("id를 숫자로 입력해주세요.");
            return;
        }

        boolean deleted = wiseSayingService.delete(id);

        if (!deleted) {
            System.out.println("%d번 명언은 존재하지 않습니다.".formatted(id));
            return;
        }

        System.out.println("%d번 명언이 삭제되었습니다.".formatted(id));
    }

    public void actionModify(Rq rq) {
        int id = rq.getParamAsInt("id", -1);

        if (id == -1) {
            System.out.println("id를 숫자로 입력해주세요.");
            return;
        }

        WiseSaying wiseSaying = wiseSayingService.findById(id);

        if (wiseSaying == null) {
            System.out.println("%d번 명언은 존재하지 않습니다.".formatted(id));
            return;
        }

        System.out.printf("명언(기존) : %s\n", wiseSaying.getContent());
        System.out.print("명언 : ");
        String content = scanner.nextLine().trim();

        System.out.printf("작가(기존) : %s\n", wiseSaying.getAuthor());
        System.out.print("작가 : ");
        String author = scanner.nextLine().trim();

        wiseSayingService.modify(wiseSaying, content, author);
    }

    public void actionBuild() {
        wiseSayingService.build();

        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }
}
