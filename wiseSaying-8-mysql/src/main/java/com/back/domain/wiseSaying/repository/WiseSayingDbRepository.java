package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.standard.dto.Page;
import com.back.standard.simpleDb.SimpleDb;
import com.back.standard.simpleDb.Sql;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// MySQL 의 wise_saying 테이블에 저장한다. SQL 은 이 클래스에만 있다.
public class WiseSayingDbRepository implements WiseSayingRepository {
    private final SimpleDb simpleDb;
    private final String dbDirPath;

    public WiseSayingDbRepository(SimpleDb simpleDb, String dbDirPath) {
        this.simpleDb = simpleDb;
        this.dbDirPath = dbDirPath;
    }

    @Override
    public void save(WiseSaying wiseSaying) {
        if (wiseSaying.getId() == 0) {
            long id = simpleDb.genSql()
                    .append("INSERT INTO wise_saying")
                    .append("SET createDate = NOW(), modifyDate = NOW()")
                    .append(", content = ?", wiseSaying.getContent())
                    .append(", author = ?", wiseSaying.getAuthor())
                    .insert();

            wiseSaying.setId((int) id);
            return;
        }

        simpleDb.genSql()
                .append("UPDATE wise_saying")
                .append("SET modifyDate = NOW()")
                .append(", content = ?", wiseSaying.getContent())
                .append(", author = ?", wiseSaying.getAuthor())
                .append("WHERE id = ?", wiseSaying.getId())
                .update();
    }

    @Override
    public WiseSaying findById(int id) {
        Map<String, Object> row = simpleDb.genSql()
                .append("SELECT * FROM wise_saying WHERE id = ?", id)
                .selectRow();

        return row == null ? null : toWiseSaying(row);
    }

    @Override
    public boolean deleteById(int id) {
        return simpleDb.genSql()
                .append("DELETE FROM wise_saying WHERE id = ?", id)
                .delete() > 0;
    }

    @Override
    public List<WiseSaying> findAll() {
        return simpleDb.genSql()
                .append("SELECT * FROM wise_saying ORDER BY id DESC")
                .selectRows()
                .stream()
                .map(this::toWiseSaying)
                .toList();
    }

    @Override
    public int count() {
        return simpleDb.genSql().append("SELECT COUNT(*) FROM wise_saying").selectLong().intValue();
    }

    // 검색·페이징을 SQL(WHERE LIKE, ORDER BY, LIMIT/OFFSET)로 처리한다.
    @Override
    public Page<WiseSaying> findForList(String keywordType, String keyword, int page, int pageSize) {
        String column = keywordType.equals("author") ? "author" : "content"; // 컬럼 이름은 바인딩이 안 되니 화이트리스트로 고른다.

        Sql countSql = simpleDb.genSql().append("SELECT COUNT(*) FROM wise_saying");
        Sql listSql = simpleDb.genSql().append("SELECT * FROM wise_saying");

        if (!keyword.isEmpty()) {
            countSql.append("WHERE " + column + " LIKE CONCAT('%', ?, '%')", keyword);
            listSql.append("WHERE " + column + " LIKE CONCAT('%', ?, '%')", keyword);
        }

        int totalCount = countSql.selectLong().intValue();

        List<WiseSaying> content = listSql
                .append("ORDER BY id DESC")
                .append("LIMIT ? OFFSET ?", pageSize, Math.max(0, (page - 1) * pageSize))
                .selectRows()
                .stream()
                .map(this::toWiseSaying)
                .toList();

        return new Page<>(content, totalCount, page, pageSize);
    }

    // 모든 명언을 번호 오름차순 배열로 {dbDir}/wiseSaying/data.json 에 쓴다.
    @Override
    public void build() {
        String json = findAll().reversed().stream()
                .map(wiseSaying -> WiseSayingFileRepository.toJson(wiseSaying).indent(2).stripTrailing())
                .collect(Collectors.joining(",\n", "[\n", "\n]"));

        Path path = Path.of(dbDirPath, "wiseSaying", "data.json");

        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, json.equals("[\n\n]") ? "[]" : json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private WiseSaying toWiseSaying(Map<String, Object> row) {
        WiseSaying wiseSaying = new WiseSaying((String) row.get("content"), (String) row.get("author"));
        wiseSaying.setId(((Number) row.get("id")).intValue());
        return wiseSaying;
    }
}
