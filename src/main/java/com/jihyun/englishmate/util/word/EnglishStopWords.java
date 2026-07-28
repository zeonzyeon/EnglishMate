package com.jihyun.englishmate.util.word;

import java.util.Set;

/**
 * 단어 추출에서 제외할 기본 영어 불용어 목록입니다.
 */
public final class EnglishStopWords {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the",
            "is", "are", "am", "was", "were", "be", "been", "being",
            "do", "does", "did",
            "have", "has", "had",
            "i", "you", "he", "she", "it", "we", "they",
            "me", "him", "her", "us", "them",
            "my", "your", "his", "its", "our", "their",
            "to", "of", "in", "on", "at", "for", "with", "from", "by", "about",
            "and", "or", "but", "so", "because", "if", "when", "while",
            "this", "that", "these", "those",
            "there", "here",
            "as", "than", "then", "too", "very", "can", "could", "will", "would",
            "should", "may", "might", "must", "not", "no", "yes"
    );

    private EnglishStopWords() {
    }

    public static boolean contains(String word) {
        return STOP_WORDS.contains(word);
    }
}
