package com.ll.simpleDb;

import java.sql.*;
import java.util.List;

public class SimpleDb {
    private final String url;
    private final String username;
    private final String password;
    private boolean devMode;

    // 스레드마다 자기 커넥션을 쓴다. (멀티스레드 안전, 트랜잭션도 스레드 단위)
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public SimpleDb(String host, String username, String password, String dbName) {
        this.url = "jdbc:mysql://%s:3306/%s?characterEncoding=UTF-8&serverTimezone=Asia/Seoul".formatted(host, dbName);
        this.username = username;
        this.password = password;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public Sql genSql() {
        return new Sql(this);
    }

    // DDL, INSERT/UPDATE/DELETE 실행. 영향받은 행 수를 돌려준다.
    public int run(String sql, Object... params) {
        return execute(sql, List.of(params), PreparedStatement::executeUpdate);
    }

    public void startTransaction() {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void commit() {
        endTransaction(true);
    }

    public void rollback() {
        endTransaction(false);
    }

    private void endTransaction(boolean commit) {
        try {
            Connection connection = getConnection();
            if (commit) connection.commit();
            else connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 현재 스레드의 커넥션을 닫는다.
    public void close() {
        Connection connection = connectionHolder.get();
        connectionHolder.remove();

        if (connection == null) return;

        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = connectionHolder.get();

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
            connectionHolder.set(connection);
        }

        return connection;
    }

    @FunctionalInterface
    interface StatementAction<T> {
        T apply(PreparedStatement statement) throws SQLException;
    }

    <T> T execute(String sql, List<Object> params, StatementAction<T> action) {
        if (devMode) {
            System.out.println("== rawSql ==\n" + sql.trim() + "\n== params ==\n" + params);
        }

        try (PreparedStatement statement = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            return action.apply(statement);
        } catch (SQLException e) {
            throw new RuntimeException("SQL 실행 실패 : " + sql, e);
        }
    }
}
