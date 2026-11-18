package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 명언 1개 = 파일 1개 ({dir}/{번호}.json), 마지막 번호 = {dir}/lastId.txt
public class WiseSayingFileRepository implements WiseSayingRepository {
    private final Path dir;

    public WiseSayingFileRepository(String dbDirPath) {
        this.dir = Path.of(dbDirPath, "wiseSaying");
    }

    @Override
    public void save(WiseSaying wiseSaying) {
        if (wiseSaying.getId() == 0) {
            int newId = getLastId() + 1;
            wiseSaying.setId(newId);
            writeString(dir.resolve("lastId.txt"), String.valueOf(newId));
        }

        writeString(jsonPath(wiseSaying.getId()), toJson(wiseSaying));
    }

    @Override
    public WiseSaying findById(int id) {
        Path path = jsonPath(id);

        if (!Files.exists(path)) return null;

        return fromJson(readString(path));
    }

    @Override
    public boolean deleteById(int id) {
        try {
            return Files.deleteIfExists(jsonPath(id));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<WiseSaying> findAll() {
        if (!Files.exists(dir)) return List.of();

        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(path -> path.getFileName().toString().matches("\\d+\\.json"))
                    .map(path -> fromJson(readString(path)))
                    .sorted(Comparator.comparingInt(WiseSaying::getId).reversed())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int getLastId() {
        Path path = dir.resolve("lastId.txt");

        if (!Files.exists(path)) return 0;

        return Integer.parseInt(readString(path).trim());
    }

    // 모든 명언을 번호 오름차순 배열로 {dir}/data.json 에 쓴다.
    @Override
    public void build() {
        List<WiseSaying> wiseSayings = findAll().reversed();

        String json = wiseSayings.stream()
                .map(wiseSaying -> toJson(wiseSaying).indent(2).stripTrailing())
                .collect(Collectors.joining(",\n", "[\n", "\n]"));

        writeString(dir.resolve("data.json"), wiseSayings.isEmpty() ? "[]" : json);
    }

    private Path jsonPath(int id) {
        return dir.resolve(id + ".json");
    }

    // ===== JSON 직접 만들기 / 읽기 =====

    static String toJson(WiseSaying wiseSaying) {
        return """
                {
                  "id": %d,
                  "content": "%s",
                  "author": "%s"
                }""".formatted(wiseSaying.getId(), escape(wiseSaying.getContent()), escape(wiseSaying.getAuthor()));
    }

    static WiseSaying fromJson(String json) {
        WiseSaying wiseSaying = new WiseSaying(
                unescape(find(json, "\"content\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")),
                unescape(find(json, "\"author\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""))
        );
        wiseSaying.setId(Integer.parseInt(find(json, "\"id\"\\s*:\\s*(\\d+)")));

        return wiseSaying;
    }

    private static String find(String json, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(json);

        if (!matcher.find()) throw new IllegalArgumentException("잘못된 JSON : " + json);

        return matcher.group(1);
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static String readString(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeString(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
