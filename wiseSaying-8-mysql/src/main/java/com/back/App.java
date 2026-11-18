package com.back;

import com.back.domain.system.controller.SystemController;
import com.back.domain.wiseSaying.controller.WiseSayingController;

import java.util.Scanner;

public class App {
    private final Scanner scanner;
    private final boolean initSampleData;

    // 실제 실행(Main) : 명언이 0개면 샘플 10개를 만든다.
    public App() {
        this(new Scanner(System.in), true);
    }

    // 테스트 기본값 : 샘플 없이 시작한다.
    public App(Scanner scanner) {
        this(scanner, false);
    }

    public App(Scanner scanner, boolean initSampleData) {
        this.scanner = scanner;
        this.initSampleData = initSampleData;
    }

    // 실행할 때마다 MySQL 에서 명언을 다시 읽는다.
    public void run() {
        System.out.println("== 명언 앱 ==");

        SystemController systemController = new SystemController();
        WiseSayingController wiseSayingController = new WiseSayingController(scanner);

        if (initSampleData) wiseSayingController.makeSampleDataIfEmpty();

        while (true) {
            System.out.print("명령) ");
            String cmd = scanner.nextLine().trim();
            Rq rq = new Rq(cmd);

            switch (rq.getActionName()) {
                case "종료" -> {
                    systemController.actionExit();
                    return;
                }
                case "목록" -> wiseSayingController.actionList(rq);
                case "등록" -> wiseSayingController.actionWrite();
                case "삭제" -> wiseSayingController.actionDelete(rq);
                case "수정" -> wiseSayingController.actionModify(rq);
                case "빌드" -> wiseSayingController.actionBuild();
            }
        }
    }
}
