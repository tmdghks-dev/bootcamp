package com.back;

public class Calc {
    public static int run(String expression) {
        // 뺄셈은 음수 덧셈으로 바꾼다: "10 - 20" -> "10 + -20", "5 - (1 + 5)" -> "5 + -(1 + 5)"
        String exp = expression.trim().replace("- ", "+ -");

        // 괄호 밖에 있는 연산자만 본다. 우선순위가 낮은 + 부터 나눠서 양쪽을 재귀로 계산
        int plus = lastTopLevel(exp, " + ");
        if (plus >= 0) {
            return run(exp.substring(0, plus)) + run(exp.substring(plus + 3));
        }

        int mul = lastTopLevel(exp, " * ");
        if (mul >= 0) {
            return run(exp.substring(0, mul)) * run(exp.substring(mul + 3));
        }

        // 괄호 앞 음수: "-(10 + 5)" -> -run("(10 + 5)")
        if (exp.startsWith("-(")) {
            return -run(exp.substring(1));
        }

        // 바깥 괄호 벗기기: "((10 + 20))" -> run("(10 + 20)")
        if (exp.startsWith("(") && exp.endsWith(")")) {
            return run(exp.substring(1, exp.length() - 1));
        }

        return Integer.parseInt(exp);
    }

    // 괄호 깊이가 0인 위치에서 op 가 마지막으로 나오는 인덱스, 없으면 -1
    private static int lastTopLevel(String exp, String op) {
        int depth = 0;
        int found = -1;

        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && exp.startsWith(op, i)) found = i;
        }

        return found;
    }
}
