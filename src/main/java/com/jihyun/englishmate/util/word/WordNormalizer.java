package com.jihyun.englishmate.util.word;

import java.util.Map;
import java.util.Set;

/**
 * 단순 규칙 기반으로 영어 단어를 기본형에 가깝게 정규화합니다.
 */
public final class WordNormalizer {

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
            "as",
            "teacher"
    );

    private static final Map<String, String> ING_RESTORE_WORDS = Map.of(
            "mak", "make",
            "writ", "write",
            "tak", "take",
            "giv", "give",
            "driv", "drive"
    );

    private WordNormalizer() {
    }

    public static String normalize(String input) {
        String cleaned = clean(input);

        if (cleaned.isBlank()) {
            return "";
        }

        String irregular = IRREGULAR_WORDS.get(cleaned);
        if (irregular != null) {
            return irregular;
        }

        if (NO_S_TRIM_WORDS.contains(cleaned)) {
            return cleaned;
        }

        return normalizeRegularWord(cleaned);
    }

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

        if (word.endsWith("er") && word.length() > 5) {
            return normalizeComparative(word.substring(0, word.length() - 2));
        }

        if (word.endsWith("es") && shouldRemoveEs(word)) {
            return word.substring(0, word.length() - 2);
        }

        if (word.endsWith("s") && word.length() > 3 && !word.endsWith("ss")) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }

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

    private static String normalizeComparative(String stem) {
        if (hasTrailingDoubleConsonant(stem)) {
            return stem.substring(0, stem.length() - 1);
        }

        return stem;
    }

    private static boolean shouldRemoveEs(String word) {
        return word.endsWith("ses")
                || word.endsWith("xes")
                || word.endsWith("zes")
                || word.endsWith("ches")
                || word.endsWith("shes");
    }

    private static boolean hasTrailingDoubleConsonant(String stem) {
        if (stem.length() < 2) {
            return false;
        }

        char last = stem.charAt(stem.length() - 1);
        char previous = stem.charAt(stem.length() - 2);
        return last == previous && "bcdfghjklmnpqrstvwxyz".indexOf(last) >= 0;
    }
}
