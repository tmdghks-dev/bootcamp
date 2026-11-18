package com.back;

import com.back.standard.simpleDb.SimpleDb;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

// 테스트는 테스트 DB(wiseSaying__test)를 쓰고, 매 테스트 전에 TRUNCATE 로 비운다.
// 그래서 테스트마다 저장소와 번호가 초기화되고, 실행 순서와 상관없이 결과가 같다.
class WiseSayingControllerTest {
    private static SimpleDb simpleDb;

    @BeforeAll
    static void beforeAll() throws IOException {
        AppConfig.setTestMode();
        simpleDb = AppConfig.getSimpleDb();

        String schema = Files.readString(Path.of("sql/schema.sql"));
        String createTable = schema.substring(schema.indexOf("CREATE TABLE"), schema.indexOf(";", schema.indexOf("CREATE TABLE")));
        simpleDb.run(createTable);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        AppConfig.setTestMode();
        simpleDb.run("TRUNCATE wise_saying");
        deleteDir(Path.of(AppConfig.getDbDirPath()));
    }

    private static long dbCount() {
        return simpleDb.genSql().append("SELECT COUNT(*) FROM wise_saying").selectLong();
    }

    static void deleteDir(Path dir) throws IOException {
        if (!Files.exists(dir)) return;

        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
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

    // ===== 15단계 : MySQL 저장 (저장·재시작, 번호 유지) =====

    @Test
    @DisplayName("등록하면 wise_saying 테이블에 행이 생긴다")
    void t17() {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                종료
                """);

        var row = simpleDb.genSql().append("SELECT * FROM wise_saying WHERE id = ?", 1).selectRow();

        assertThat(dbCount()).isEqualTo(1);
        assertThat(row.get("content")).isEqualTo("현재를 사랑하라.");
        assertThat(row.get("author")).isEqualTo("작자미상");
    }

    @Test
    @DisplayName("재시작해도 목록이 남아 있다")
    void t18() {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                종료
                """);

        String out = AppTestRunner.run("""
                목록
                종료
                """);

        assertThat(out)
                .contains("2 / 작자미상 / 과거에 집착하지 마라.")
                .contains("1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("수정 후 재시작해도 수정된 내용이 남아 있다")
    void t19() {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                수정?id=1
                현재와 자신을 사랑하라.
                홍길동
                종료
                """);

        String out = AppTestRunner.run("""
                목록
                종료
                """);

        assertThat(out).contains("1 / 홍길동 / 현재와 자신을 사랑하라.");
        assertThat(simpleDb.genSql().append("SELECT author FROM wise_saying WHERE id = ?", 1).selectString()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("삭제하면 행도 지워지고, 재시작 후 다음 번호는 삭제된 번호를 재사용하지 않는다")
    void t20() {
        AppTestRunner.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                삭제?id=2
                종료
                """);

        assertThat(dbCount()).isEqualTo(1);

        String out = AppTestRunner.run("""
                등록
                너 자신을 알라.
                소크라테스
                목록
                종료
                """);

        assertThat(out)
                .contains("3번 명언이 등록되었습니다.")
                .contains("3 / 소크라테스 / 너 자신을 알라.")
                .doesNotContain("2 / 작자미상");
    }

    @Test
    @DisplayName("따옴표가 들어간 명언도 저장하고 다시 읽을 수 있다")
    void t21() {
        AppTestRunner.run("""
                등록
                "삶이 있는 한 희망은 있다"
                키케로
                종료
                """);

        String out = AppTestRunner.run("""
                목록
                종료
                """);

        assertThat(out).contains("1 / 키케로 / \"삶이 있는 한 희망은 있다\"");
    }

    // ===== 10단계 : 빌드 =====

    @Test
    @DisplayName("빌드 : 모든 명언을 data.json 배열로 만든다")
    void t22() throws IOException {
        String out = AppTestRunner.run("""
                등록
                명언 1
                작가 1
                등록
                명언 2
                작가 2
                빌드
                종료
                """);

        assertThat(out).contains("data.json 파일의 내용이 갱신되었습니다.");
        assertThat(Files.readString(Path.of("db_test/wiseSaying/data.json"))).isEqualTo("""
                [
                  {
                    "id": 1,
                    "content": "명언 1",
                    "author": "작가 1"
                  },
                  {
                    "id": 2,
                    "content": "명언 2",
                    "author": "작가 2"
                  }
                ]""");
    }
}
