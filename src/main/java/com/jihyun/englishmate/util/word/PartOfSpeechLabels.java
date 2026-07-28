package com.jihyun.englishmate.util.word;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 품사 저장값과 사용자에게 보여줄 한글 포함 표시명을 관리합니다.
 */
public final class PartOfSpeechLabels {

    private static final List<Option> OPTIONS = List.of(
            new Option("Noun", "Noun (명사)"),
            new Option("Verb", "Verb (동사)"),
            new Option("Adjective", "Adjective (형용사)"),
            new Option("Adverb", "Adverb (부사)"),
            new Option("Pronoun", "Pronoun (대명사)"),
            new Option("Preposition", "Preposition (전치사)"),
            new Option("Conjunction", "Conjunction (접속사)"),
            new Option("Interjection", "Interjection (감탄사)"),
            new Option("Other", "Other (기타)")
    );

    private static final Map<String, String> LABELS = OPTIONS.stream()
            .collect(Collectors.toUnmodifiableMap(Option::value, Option::label));

    private PartOfSpeechLabels() {
    }

    public static List<Option> options() {
        return OPTIONS;
    }

    public static String labelOf(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }

        return LABELS.getOrDefault(value, value);
    }

    public record Option(
            String value,
            String label
    ) {
    }
}
