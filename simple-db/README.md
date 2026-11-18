# simple-db

JDBC 를 감싼 SQL 실행 유틸리티 `SimpleDb`, `Sql` (데브코스 과제 : MySQL/JDBC 유틸리티 SimpleDb 구현)

## 실행

```bash
docker run --name mysql-1 -p 127.0.0.1:3306:3306 -e TZ=Asia/Seoul -e MYSQL_ROOT_PASSWORD=lldj123414 -d mysql:8.4
docker exec mysql-1 mysql -uroot -plldj123414 -e "CREATE DATABASE IF NOT EXISTS simpleDb__test CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
./gradlew test
```

## 구조

- `SimpleDb` : 접속 정보, `run`, `genSql`, 트랜잭션(`startTransaction`/`commit`/`rollback`), `close`
  - 커넥션을 `ThreadLocal` 에 둬서 스레드마다 자기 커넥션을 쓴다.
- `Sql` : `append`/`appendIn` 체이닝 + `?` 바인딩, `insert`/`update`/`delete`, `selectRows`/`selectRow`(Map 또는 클래스), `selectDatetime`/`selectLong`/`selectLongs`/`selectString`/`selectBoolean`
  - 클래스 매핑은 jackson `ObjectMapper.convertValue(row, cls)` 로 한다.
