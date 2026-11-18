package com.back;

import com.back.standard.simpleDb.SimpleDb;

// 실제 실행은 개발 DB(wiseSaying__dev), 테스트는 테스트 DB(wiseSaying__test)를 쓴다.
// 접속 정보는 환경변수에서 읽는다. (DB_PASSWORD 는 필수)
public class AppConfig {
    private static String mode = "dev";
    private static SimpleDb devSimpleDb;
    private static SimpleDb testSimpleDb;

    public static void setDevMode() {
        mode = "dev";
    }

    public static void setTestMode() {
        mode = "test";
    }

    public static boolean isTestMode() {
        return mode.equals("test");
    }

    // data.json 을 쓰는 폴더
    public static String getDbDirPath() {
        return isTestMode() ? "db_test" : "db";
    }

    public static synchronized SimpleDb getSimpleDb() {
        if (isTestMode()) {
            if (testSimpleDb == null) testSimpleDb = genSimpleDb("wiseSaying__test");
            return testSimpleDb;
        }

        if (devSimpleDb == null) devSimpleDb = genSimpleDb("wiseSaying__dev");
        return devSimpleDb;
    }

    private static SimpleDb genSimpleDb(String defaultDbName) {
        String password = System.getenv("DB_PASSWORD");

        if (password == null) {
            throw new IllegalStateException("환경변수 DB_PASSWORD 를 설정하세요. (README 참고)");
        }

        String dbName = isTestMode()
                ? env("DB_TEST_NAME", defaultDbName)
                : env("DB_NAME", defaultDbName);

        return new SimpleDb(env("DB_HOST", "localhost"), env("DB_USERNAME", "root"), password, dbName);
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
