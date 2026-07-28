package com.jihyun.englishmate.repository.word;

import com.jihyun.englishmate.entity.word.MaterialWord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 학습 지문과 추출 단어의 연결 정보를 관리합니다.
 */
public interface MaterialWordRepository extends JpaRepository<MaterialWord, Long> {

    boolean existsByStudyMaterialIdAndWordId(Long studyMaterialId, Long wordId);
}
