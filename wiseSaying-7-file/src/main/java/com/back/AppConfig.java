package com.back;

// 실제 실행은 db/, 테스트는 db_test/ 를 쓴다.
public class AppConfig {
    private static String mode = "dev";

    public static void setDevMode() {
        mode = "dev";
    }

    public static void setTestMode() {
        mode = "test";
    }

    public static String getDbDirPath() {
        return mode.equals("test") ? "db_test" : "db";
    }
}
