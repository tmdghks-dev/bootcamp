package com.back;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("== 명언 앱 ==");
        Scanner sc = new Scanner(System.in);

        int count = 0;
        List<WiseSaying> list = new ArrayList<>();

        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            if (cmd.equals("종료")) {
                break;
            } else if (cmd.equals("등록")) {
                System.out.print("명언 : ");
                String say = sc.nextLine();
                System.out.print("작가 : ");
                String author = sc.nextLine();

                ++count;
                WiseSaying ws = new WiseSaying(count, author, say);
                list.add(ws);
                System.out.println(count + "번 명언이 등록되었습니다.");

            } else if (cmd.equals("목록")) {
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");
                for (int i = list.size() - 1; i >= 0; i--) {
                    WiseSaying ws = list.get(i);
                    System.out.println(ws.id + " / " + ws.author + " / " + ws.say);
                }

            } else if (cmd.startsWith("삭제?id=")) {
                int id = Integer.parseInt(cmd.substring(6));
                boolean found = false;

                for (int i = 0; i < list.size(); i++) {
                    WiseSaying ws = list.get(i);
                    if (ws.id == id) {
                        list.remove(i);
                        System.out.println(id + "번 명언이 삭제되었습니다.");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println(id + "번 명언은 존재하지 않습니다.");
                }

            } else if (cmd.startsWith("수정?id=")) {
                int id = Integer.parseInt(cmd.substring(6));

                WiseSaying target = null;
                for (int i = 0; i < list.size(); i++) {
                    WiseSaying ws = list.get(i);
                    if (ws.id == id) {
                        target = ws;
                        break;
                    }
                }

                if (target == null) {
                    System.out.println(id + "번 명언은 존재하지 않습니다.");
                } else {
                    System.out.println("명언(기존) : " + target.say);
                    System.out.print("명언 : ");
                    String say = sc.nextLine();

                    System.out.println("작가(기존) : " + target.author);
                    System.out.print("작가 : ");
                    String author = sc.nextLine();

                    target.say = say;
                    target.author = author;
                }
            }
        }

        sc.close();
    }
}