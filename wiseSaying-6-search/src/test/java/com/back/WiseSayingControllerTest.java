package com.back;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// AppTestRunner.run() 은 매번 new App() 을 만들고, App 은 실행할 때마다 빈 저장소를 새로 만든다.
// 그래서 테스트마다 저장소와 번호가 초기화되고, 실행 순서와 상관없이 결과가 같다.
class WiseSayingControllerTest {
    @Test
    @DisplayName("종료")
    void t1() {
        String out = AppTestRunner.run("""
                종료
                """);

        assertThat(out)
                .contains("== 명언 앱 ==")
                .contains("명령) ")
                .contains("프로그램이 종료합니다.");
    }

    @Test
    @DisplayName("등록 : 명언과 작가를 입력받는다")
    void t2() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                종료
                """);

        assertThat(out).contains("명언 : ", "작가 : ");
    }

    @Test
    @DisplayName("등록")
    void t3() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                종료
                """);

        assertThat(out).contains("명언 :", "작가 :", "1번 명언이 등록되었습니다.");
    }

    @Test
    @DisplayName("등록할 때마다 번호가 1씩 증가한다")
    void t4() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                종료
                """);

        assertThat(out)
                .contains("1번 명언이 등록되었습니다.")
                .contains("2번 명언이 등록되었습니다.");
    }

    @Test
    @DisplayName("목록 : 최신순으로 보여준다")
    void t5() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                목록
                종료
                """);

        assertThat(out)
                .contains("번호 / 작가 / 명언")
                .contains("----------------------")
                .contains("2 / 작자미상 / 과거에 집착하지 마라.")
                .contains("1 / 작자미상 / 현재를 사랑하라.");
        assertThat(out.indexOf("2 / 작자미상")).isLessThan(out.indexOf("1 / 작자미상"));
    }

    @Test
    @DisplayName("삭제")
    void t6() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                삭제?id=1
                목록
                종료
                """);

        assertThat(out)
                .contains("1번 명언이 삭제되었습니다.")
                .contains("2 / 작자미상 / 과거에 집착하지 마라.")
                .doesNotContain("1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("없는 번호 삭제")
    void t7() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                삭제?id=1
                삭제?id=1
                종료
                """);

        assertThat(out)
                .contains("1번 명언이 삭제되었습니다.")
                .contains("1번 명언은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("수정")
    void t8() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                수정?id=1
                현재와 자신을 사랑하라.
                홍길동
                목록
                종료
                """);

        assertThat(out)
                .contains("명언(기존) : 현재를 사랑하라.")
                .contains("작가(기존) : 작자미상")
                .contains("1 / 홍길동 / 현재와 자신을 사랑하라.")
                .doesNotContain("1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("없는 번호 수정")
    void t9() {
        String out = AppTestRunner.run("""
                수정?id=3
                종료
                """);

        assertThat(out).contains("3번 명언은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("다른 테스트에서 등록한 명언이 남아 있지 않다 (저장소 초기화)")
    void t10() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                종료
                """);

        assertThat(out).contains("1번 명언이 등록되었습니다.");
    }

    // ===== 13단계 : 검색 =====

    @Test
    @DisplayName("목록?keywordType=content&keyword=과거")
    void t11() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                목록?keywordType=content&keyword=과거
                종료
                """);

        assertThat(out)
                .contains("검색타입 : content")
                .contains("검색어 : 과거")
                .contains("2 / 작자미상 / 과거에 집착하지 마라.")
                .doesNotContain("1 / 작자미상 / 현재를 사랑하라.");
        assertThat(out.indexOf("검색타입 : content")).isLessThan(out.indexOf("번호 / 작가 / 명언"));
    }

    @Test
    @DisplayName("목록?keywordType=author&keyword=작자")
    void t12() {
        String out = AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                홍길동
                목록?keywordType=author&keyword=작자
                종료
                """);

        assertThat(out)
                .contains("검색타입 : author")
                .contains("검색어 : 작자")
                .contains("1 / 작자미상 / 현재를 사랑하라.")
                .doesNotContain("2 / 홍길동 / 과거에 집착하지 마라.");
    }

    // ===== 14단계 : 페이징 (샘플 10개로 시작) =====

    private static String runWithSample(String input) {
        return AppTestRunner.run(input, scanner -> new App(scanner, true).run());
    }

    @Test
    @DisplayName("명언이 0개면 샘플 10개를 만들고, 목록은 1페이지(10~6번)를 보여준다")
    void t13() {
        String out = runWithSample("""
                목록
                종료
                """);

        assertThat(out)
                .contains("10 / 작자미상 10 / 명언 10")
                .contains("6 / 작자미상 6 / 명언 6")
                .doesNotContain("5 / 작자미상 5 / 명언 5")
                .contains("페이지 : [1] / 2");
        assertThat(out.indexOf("10 / 작자미상 10")).isLessThan(out.indexOf("6 / 작자미상 6"));
    }

    @Test
    @DisplayName("목록?page=2 는 5~1번")
    void t14() {
        String out = runWithSample("""
                목록?page=2
                종료
                """);

        assertThat(out)
                .contains("5 / 작자미상 5 / 명언 5")
                .contains("1 / 작자미상 1 / 명언 1")
                .doesNotContain("6 / 작자미상 6 / 명언 6")
                .contains("페이지 : 1 / [2]");
    }

    @Test
    @DisplayName("내용에 1 검색 : 10번, 1번")
    void t15() {
        String out = runWithSample("""
                목록?keywordType=content&keyword=1
                종료
                """);

        assertThat(out)
                .contains("10 / 작자미상 10 / 명언 10")
                .contains("1 / 작자미상 1 / 명언 1")
                .doesNotContain("2 / 작자미상 2 / 명언 2")
                .contains("페이지 : [1]");
        assertThat(out.indexOf("10 / 작자미상 10")).isLessThan(out.indexOf("1 / 작자미상 1 /"));
    }

    @Test
    @DisplayName("검색 결과를 기준으로 페이징 : 작가에 작자 검색 2페이지")
    void t16() {
        String out = runWithSample("""
                등록
                현재를 사랑하라.
                홍길동
                목록?keywordType=author&keyword=작자&page=2
                종료
                """);

        assertThat(out)
                .contains("5 / 작자미상 5 / 명언 5")
                .contains("1 / 작자미상 1 / 명언 1")
                .doesNotContain("11 / 홍길동")
                .contains("페이지 : 1 / [2]");
    }
}
