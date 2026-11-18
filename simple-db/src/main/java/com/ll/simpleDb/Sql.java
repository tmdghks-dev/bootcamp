package com.ll.simpleDb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Sql {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final SimpleDb simpleDb;
    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<Object> params = new ArrayList<>();

    Sql(SimpleDb simpleDb) {
        this.simpleDb = simpleDb;
    }

    public Sql append(String sqlPart, Object... params) {
        sqlBuilder.append(sqlPart).append('\n');
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    // "IN (?)" 의 ? 하나를 값 개수만큼 "?, ?, ?" 로 펼친다.
    public Sql appendIn(String sqlPart, Object... params) {
        String placeholders = Arrays.stream(params).map(p -> "?").collect(Collectors.joining(", "));
        return append(sqlPart.replace("?", placeholders), params);
    }

    // AUTO_INCREMENT 로 생성된 주키
    public long insert() {
        return simpleDb.execute(sql(), params, statement -> {
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : -1L;
            }
        });
    }

    public int update() {
        return simpleDb.execute(sql(), params, java.sql.PreparedStatement::executeUpdate);
    }

    public int delete() {
        return update();
    }

    public List<Map<String, Object>> selectRows() {
        return simpleDb.execute(sql(), params, statement -> {
            try (ResultSet rs = statement.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                List<Map<String, Object>> rows = new ArrayList<>();

                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }

                return rows;
            }
        });
    }

    public Map<String, Object> selectRow() {
        List<Map<String, Object>> rows = selectRows();
        return rows.isEmpty() ? null : rows.getFirst();
    }

    public <T> List<T> selectRows(Class<T> cls) {
        return selectRows().stream().map(row -> objectMapper.convertValue(row, cls)).toList();
    }

    public <T> T selectRow(Class<T> cls) {
        Map<String, Object> row = selectRow();
        return row == null ? null : objectMapper.convertValue(row, cls);
    }

    public LocalDateTime selectDatetime() {
        return (LocalDateTime) selectValue();
    }

    public Long selectLong() {
        Object value = selectValue();
        return value == null ? null : ((Number) value).longValue();
    }

    public List<Long> selectLongs() {
        return selectRows().stream()
                .map(row -> ((Number) row.values().iterator().next()).longValue())
                .toList();
    }

    public String selectString() {
        Object value = selectValue();
        return value == null ? null : value.toString();
    }

    // BIT(1) 은 Boolean, "SELECT 1 = 1" 은 숫자(1/0)로 온다.
    public Boolean selectBoolean() {
        Object value = selectValue();
        if (value == null) return null;
        if (value instanceof Boolean b) return b;
        return ((Number) value).longValue() != 0;
    }

    // 첫 행의 첫 컬럼
    private Object selectValue() {
        Map<String, Object> row = selectRow();
        return row == null ? null : row.values().iterator().next();
    }

    private String sql() {
        return sqlBuilder.toString();
    }
}
