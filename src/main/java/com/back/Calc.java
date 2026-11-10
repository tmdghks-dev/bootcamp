package com.back;

public class Calc {
    public static int run(String expression) {
        // 뺄셈은 음수 덧셈으로 바꾼다: "10 - 20" -> "10 + -20"
        String exp = expression.trim().replace("- ", "+ -");

        // 우선순위가 낮은 + 부터 나눠서 양쪽을 재귀로 계산
        int plus = exp.lastIndexOf(" + ");
        if (plus >= 0) {
            return run(exp.substring(0, plus)) + run(exp.substring(plus + 3));
        }

        int mul = exp.lastIndexOf(" * ");
        if (mul >= 0) {
            return run(exp.substring(0, mul)) * run(exp.substring(mul + 3));
        }

        return Integer.parseInt(exp);
    }
}
