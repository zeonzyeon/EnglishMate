package com.jihyun.englishmate.util.word;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WordNormalizerTest {

    @Test
    @DisplayName("정상 복수형은 마지막 s를 제거한다")
    void normalizeRegularPlural() {
        assertThat(WordNormalizer.normalize("apples")).isEqualTo("apple");
        assertThat(WordNormalizer.normalize("books")).isEqualTo("book");
    }

    @Test
    @DisplayName("es 복수형은 es를 제거한다")
    void normalizeEsPlural() {
        assertThat(WordNormalizer.normalize("classes")).isEqualTo("class");
        assertThat(WordNormalizer.normalize("boxes")).isEqualTo("box");
        assertThat(WordNormalizer.normalize("watches")).isEqualTo("watch");
    }

    @Test
    @DisplayName("ies 복수형은 y로 바꾼다")
    void normalizeIesPlural() {
        assertThat(WordNormalizer.normalize("stories")).isEqualTo("story");
        assertThat(WordNormalizer.normalize("babies")).isEqualTo("baby");
    }

    @Test
    @DisplayName("과거형은 기본형에 가깝게 정규화한다")
    void normalizePastTense() {
        assertThat(WordNormalizer.normalize("studied")).isEqualTo("study");
        assertThat(WordNormalizer.normalize("carried")).isEqualTo("carry");
        assertThat(WordNormalizer.normalize("played")).isEqualTo("play");
        assertThat(WordNormalizer.normalize("worked")).isEqualTo("work");
    }

    @Test
    @DisplayName("ing형은 기본형에 가깝게 정규화한다")
    void normalizeIngForm() {
        assertThat(WordNormalizer.normalize("running")).isEqualTo("run");
        assertThat(WordNormalizer.normalize("swimming")).isEqualTo("swim");
        assertThat(WordNormalizer.normalize("making")).isEqualTo("make");
        assertThat(WordNormalizer.normalize("writing")).isEqualTo("write");
    }

    @Test
    @DisplayName("불규칙 단어는 별도 매핑으로 정규화한다")
    void normalizeIrregularWords() {
        assertThat(WordNormalizer.normalize("children")).isEqualTo("child");
        assertThat(WordNormalizer.normalize("men")).isEqualTo("man");
        assertThat(WordNormalizer.normalize("women")).isEqualTo("woman");
        assertThat(WordNormalizer.normalize("people")).isEqualTo("person");
        assertThat(WordNormalizer.normalize("mice")).isEqualTo("mouse");
        assertThat(WordNormalizer.normalize("feet")).isEqualTo("foot");
        assertThat(WordNormalizer.normalize("teeth")).isEqualTo("tooth");
        assertThat(WordNormalizer.normalize("geese")).isEqualTo("goose");
        assertThat(WordNormalizer.normalize("went")).isEqualTo("go");
        assertThat(WordNormalizer.normalize("gone")).isEqualTo("go");
        assertThat(WordNormalizer.normalize("did")).isEqualTo("do");
        assertThat(WordNormalizer.normalize("done")).isEqualTo("do");
        assertThat(WordNormalizer.normalize("had")).isEqualTo("have");
        assertThat(WordNormalizer.normalize("made")).isEqualTo("make");
        assertThat(WordNormalizer.normalize("took")).isEqualTo("take");
        assertThat(WordNormalizer.normalize("taken")).isEqualTo("take");
        assertThat(WordNormalizer.normalize("saw")).isEqualTo("see");
        assertThat(WordNormalizer.normalize("seen")).isEqualTo("see");
        assertThat(WordNormalizer.normalize("was")).isEqualTo("be");
        assertThat(WordNormalizer.normalize("were")).isEqualTo("be");
    }

    @Test
    @DisplayName("예외 단어는 마지막 s를 제거하지 않는다")
    void keepExceptionWords() {
        assertThat(WordNormalizer.normalize("news")).isEqualTo("news");
        assertThat(WordNormalizer.normalize("series")).isEqualTo("series");
        assertThat(WordNormalizer.normalize("species")).isEqualTo("species");
        assertThat(WordNormalizer.normalize("analysis")).isEqualTo("analysis");
        assertThat(WordNormalizer.normalize("basis")).isEqualTo("basis");
        assertThat(WordNormalizer.normalize("crisis")).isEqualTo("crisis");
        assertThat(WordNormalizer.normalize("class")).isEqualTo("class");
        assertThat(WordNormalizer.normalize("glass")).isEqualTo("glass");
        assertThat(WordNormalizer.normalize("bus")).isEqualTo("bus");
        assertThat(WordNormalizer.normalize("status")).isEqualTo("status");
        assertThat(WordNormalizer.normalize("process")).isEqualTo("process");
        assertThat(WordNormalizer.normalize("jealous")).isEqualTo("jealous");
        assertThat(WordNormalizer.normalize("this")).isEqualTo("this");
        assertThat(WordNormalizer.normalize("his")).isEqualTo("his");
        assertThat(WordNormalizer.normalize("is")).isEqualTo("is");
        assertThat(WordNormalizer.normalize("yes")).isEqualTo("yes");
        assertThat(WordNormalizer.normalize("us")).isEqualTo("us");
        assertThat(WordNormalizer.normalize("as")).isEqualTo("as");
    }

    @Test
    @DisplayName("소유격은 제거한다")
    void removePossessive() {
        assertThat(WordNormalizer.normalize("teacher's")).isEqualTo("teacher");
    }

    @Test
    @DisplayName("대소문자를 정규화한다")
    void normalizeCase() {
        assertThat(WordNormalizer.normalize("Apples")).isEqualTo("apple");
        assertThat(WordNormalizer.normalize("RUNNING")).isEqualTo("run");
    }

    @Test
    @DisplayName("앞뒤 구두점을 제거한다")
    void removePunctuation() {
        assertThat(WordNormalizer.normalize("\"apples,\"")).isEqualTo("apple");
        assertThat(WordNormalizer.normalize("(writing)")).isEqualTo("write");
    }

    @Test
    @DisplayName("영어 알파벳 단어가 아니면 빈 문자열을 반환한다")
    void returnEmptyForInvalidToken() {
        assertThat(WordNormalizer.normalize("1234")).isEmpty();
        assertThat(WordNormalizer.normalize("!!!")).isEmpty();
        assertThat(WordNormalizer.normalize("apple123")).isEmpty();
        assertThat(WordNormalizer.normalize("")).isEmpty();
    }
}
