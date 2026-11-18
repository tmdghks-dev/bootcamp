# 명언 앱 ⑧ MySQL 전환 (15단계)

⑦ 파일 저장을 MySQL 저장으로 바꿨다. 컨트롤러는 그대로, 서비스는 리포지터리를 만드는 한 줄만 바꿨다.

## 1. MySQL 띄우기 + DB·테이블 만들기

```bash
docker run --name mysql-1 -p 127.0.0.1:3306:3306 -e TZ=Asia/Seoul -e MYSQL_ROOT_PASSWORD=비밀번호 -d mysql:8.4
docker exec -i mysql-1 mysql -uroot -p비밀번호 < sql/schema.sql
```

[`sql/schema.sql`](sql/schema.sql) 이 개발 DB `wiseSaying__dev`, 테스트 DB `wiseSaying__test` 와 각각의 `wise_saying` 테이블을 만든다.

```sql
CREATE TABLE IF NOT EXISTS wise_saying (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),
    createDate DATETIME NOT NULL,
    modifyDate DATETIME NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL
);
```

## 2. 접속 정보 (환경변수)

비밀번호는 소스에 없다. `AppConfig` 가 환경변수에서 읽는다.

| 환경변수 | 기본값 |
|---|---|
| `DB_PASSWORD` | (필수) |
| `DB_HOST` | localhost |
| `DB_USERNAME` | root |
| `DB_NAME` | wiseSaying__dev |
| `DB_TEST_NAME` | wiseSaying__test |

## 3. 실행 / 테스트

```bash
export DB_PASSWORD=비밀번호
./gradlew run -q --console=plain
./gradlew test
```

테스트는 `AppConfig.setTestMode()` 로 테스트 DB 를 쓰고, `@BeforeEach` 에서 `TRUNCATE wise_saying` 으로 비운다.

## 리포지터리를 갈아 끼운 방법

```
WiseSayingRepository (인터페이스)
├── WiseSayingFileRepository  ⑦ db/wiseSaying/{id}.json
└── WiseSayingDbRepository    ⑧ MySQL wise_saying 테이블 (지금 사용)
```

- `WiseSayingService` 생성자에서 `new WiseSayingFileRepository(...)` → `new WiseSayingDbRepository(AppConfig.getSimpleDb(), ...)` 한 줄만 바꿨다.
- SQL 은 `WiseSayingDbRepository` 에만 있다. JDBC 는 앞 과제의 `SimpleDb` 를 `com.back.standard.simpleDb` 로 가져와 썼다.
- 검색·페이징은 SQL 로 처리한다 : `WHERE content|author LIKE CONCAT('%', ?, '%') ORDER BY id DESC LIMIT ? OFFSET ?`
- 번호는 `AUTO_INCREMENT` 라서 재시작해도 이어지고, 삭제한 번호는 재사용하지 않는다.
- `빌드` 는 전체 명언을 `db/wiseSaying/data.json` 에 배열로 쓴다.
