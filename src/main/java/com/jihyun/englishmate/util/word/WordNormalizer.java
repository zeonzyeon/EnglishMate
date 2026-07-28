package com.jihyun.englishmate.util.word;

import java.util.Map;
import java.util.Set;

/**
 * 외부 NLP 없이 단순 규칙 기반으로 영어 단어를 기본형에 가깝게 정규화합니다.
 */
public final class WordNormalizer {

    /**
     * 규칙으로 처리하기 어려운 불규칙 복수형/동사 활용을 직접 매핑합니다.
     */
    private static final Map<String, String> IRREGULAR_WORDS = Map.ofEntries(
            Map.entry("children", "child"),
            Map.entry("men", "man"),
            Map.entry("women", "woman"),
            Map.entry("people", "person"),
            Map.entry("mice", "mouse"),
            Map.entry("feet", "foot"),
            Map.entry("teeth", "tooth"),
            Map.entry("geese", "goose"),
            Map.entry("went", "go"),
            Map.entry("gone", "go"),
            Map.entry("does", "do"),
            Map.entry("did", "do"),
            Map.entry("done", "do"),
            Map.entry("had", "have"),
            Map.entry("made", "make"),
            Map.entry("took", "take"),
            Map.entry("taken", "take"),
            Map.entry("saw", "see"),
            Map.entry("seen", "see"),
            Map.entry("was", "be"),
            Map.entry("were", "be")
    );

    /**
     * er로 끝나는 모든 단어를 비교급으로 볼 수 없으므로 확실한 비교급만 직접 매핑합니다.
     */
    private static final Map<String, String> COMPARATIVE_WORDS = Map.of(
            "smarter", "smart"
    );

    /**
     * s로 끝나지만 단순히 s를 제거하면 의미가 깨지는 단어 목록입니다.
     */
    private static final Set<String> NO_S_TRIM_WORDS = Set.of(
            "news",
            "series",
            "species",
            "analysis",
            "basis",
            "crisis",
            "class",
            "glass",
            "bus",
            "status",
            "process",
            "jealous",
            "this",
            "his",
            "is",
            "yes",
            "us",
            "as"
    );

    /**
     * ing를 제거한 뒤 e를 복원해야 하는 단어의 어간 목록입니다.
     */
    private static final Map<String, String> ING_RESTORE_WORDS = Map.of(
            "mak", "make",
            "writ", "write",
            "tak", "take",
            "giv", "give",
            "driv", "drive"
    );

    private WordNormalizer() {
    }

    /**
     * 입력 단어를 정제한 뒤 불규칙, 예외, 일반 규칙 순서로 기본형을 추정합니다.
     */
    public static String normalize(String input) {
        String cleaned = clean(input);

        if (cleaned.isBlank()) {
            return "";
        }

        String irregular = IRREGULAR_WORDS.get(cleaned);
        if (irregular != null) {
            return irregular;
        }

        String comparative = COMPARATIVE_WORDS.get(cleaned);
        if (comparative != null) {
            return comparative;
        }

        if (NO_S_TRIM_WORDS.contains(cleaned)) {
            return cleaned;
        }

        return normalizeRegularWord(cleaned);
    }

    /**
     * 토큰의 앞뒤 구두점, 소유격, 대소문자를 정리하고 영어 알파벳 단어만 남깁니다.
     */
    public static String clean(String input) {
        if (input == null) {
            return "";
        }

        if (input.matches(".*\\d.*")) {
            return "";
        }

        String cleaned = input.trim()
                .toLowerCase()
                .replaceAll("^[^a-z]+|[^a-z]+$", "");

        if (cleaned.endsWith("'s")) {
            cleaned = cleaned.substring(0, cleaned.length() - 2);
        }

        cleaned = cleaned.replaceAll("^[^a-z]+|[^a-z]+$", "");

        if (!cleaned.matches("[a-z]+")) {
            return "";
        }

        return cleaned;
    }

    /**
     * 복수형, 과거형, 진행형, 비교급처럼 비교적 확실한 규칙만 적용합니다.
     */
    private static String normalizeRegularWord(String word) {
        if (word.length() <= 2) {
            return word;
        }

        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }

        if (word.endsWith("ied") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }

        if (word.endsWith("ing") && word.length() > 5) {
            return normalizeIng(word.substring(0, word.length() - 3));
        }

        if (word.endsWith("ed") && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        }

        if (word.endsWith("es") && shouldRemoveEs(word)) {
            return word.substring(0, word.length() - 2);
        }

        if (word.endsWith("s") && word.length() > 3 && !word.endsWith("ss")) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }

    /**
     * ing 제거 후 running -> run처럼 끝 중복 자음을 정리하거나, making -> make처럼 e를 복원합니다.
     */
    private static String normalizeIng(String stem) {
        String restored = ING_RESTORE_WORDS.get(stem);
        if (restored != null) {
            return restored;
        }

        if (hasTrailingDoubleConsonant(stem)) {
            return stem.substring(0, stem.length() - 1);
        }

        return stem;
    }

    /**
     * boxes, watches처럼 es를 제거해도 안전한 어미인지 확인합니다.
     */
    private static boolean shouldRemoveEs(String word) {
        return word.endsWith("ses")
                || word.endsWith("xes")
                || word.endsWith("zes")
                || word.endsWith("ches")
                || word.endsWith("shes");
    }

    /**
     * running -> run, smarter -> smart 처리를 위해 끝 글자가 같은 자음인지 확인합니다.
     */
    private static boolean hasTrailingDoubleConsonant(String stem) {
        if (stem.length() < 2) {
            return false;
        }

        char last = stem.charAt(stem.length() - 1);
        char previous = stem.charAt(stem.length() - 2);
        return last == previous && "bcdfghjklmnpqrstvwxyz".indexOf(last) >= 0;
    }
}
