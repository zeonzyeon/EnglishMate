package com.jihyun.englishmate.dto.review;

import com.jihyun.englishmate.dto.vocabulary.VocabularySourceResponse;
import com.jihyun.englishmate.entity.review.ReviewResponseType;
import java.util.List;

/**
 * 복습 완료 화면의 카드별 결과 DTO입니다.
 */
public record ReviewCardResultResponse(
        int cardOrder,
        String wordText,
        String meaning,
        ReviewResponseType responseType,
        String responseLabel,
        List<VocabularySourceResponse> sources
) {
}
