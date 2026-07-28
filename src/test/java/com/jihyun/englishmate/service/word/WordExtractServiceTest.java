package com.jihyun.englishmate.service.word;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WordExtractServiceTest {

    private final WordExtractService wordExtractService = new WordExtractService(null, null, null, null);

    @Test
    @DisplayName("정규화 전 불용어는 저장 대상에서 제외한다")
    void excludeStopWordsBeforeNormalization() {
        Map<String, WordExtractService.ExtractedWord> words = wordExtractService.extractWords(
                "this his is apples"
        );

        assertThat(words).doesNotContainKeys("this", "his", "is", "thi", "hi");
        assertThat(words).containsKey("apple");
    }

    @Test
    @DisplayName("정규화 후 불용어는 저장 대상에서 제외한다")
    void excludeStopWordsAfterNormalization() {
        Map<String, WordExtractService.ExtractedWord> words = wordExtractService.extractWords(
                "was does classes"
        );

        assertThat(words).doesNotContainKeys("was", "be", "does", "do");
        assertThat(words).containsKey("class");
    }

    @Test
    @DisplayName("복수형과 예외 단어를 정규화한다")
    void normalizePluralAndKeepExceptionWords() {
        Map<String, WordExtractService.ExtractedWord> words = wordExtractService.extractWords(
                "apples classes news"
        );

        assertThat(words).containsKeys("apple", "class", "news");
        assertThat(words).doesNotContainKey("new");
    }
}
