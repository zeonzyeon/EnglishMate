package com.jihyun.englishmate.controller.vocabulary;

import com.jihyun.englishmate.dto.vocabulary.VocabularyResponse;
import com.jihyun.englishmate.security.member.CustomUserDetails;
import com.jihyun.englishmate.service.vocabulary.VocabularyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 개인 단어장 화면 요청을 처리합니다.
 */
@Controller
@RequestMapping("/vocabulary")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;

    /**
     * 로그인한 회원의 개인 단어장 목록을 조회합니다.
     */
    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<VocabularyResponse> vocabularies = vocabularyService.findAllByMember(userDetails.getMemberId());
        model.addAttribute("vocabularies", vocabularies);
        model.addAttribute("nickname", userDetails.getNickname());
        return "vocabulary/list";
    }

    /**
     * 단어를 개인 단어장에 저장합니다.
     */
    @PostMapping("/{wordId}")
    public String addWord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long wordId,
            @RequestHeader(value = "Referer", required = false) String referer,
            RedirectAttributes redirectAttributes
    ) {
        boolean saved = vocabularyService.addWord(userDetails.getMemberId(), wordId);
        redirectAttributes.addFlashAttribute(
                saved ? "message" : "warningMessage",
                saved ? "단어장에 추가되었습니다." : "이미 단어장에 저장된 단어입니다."
        );
        return "redirect:" + (referer != null ? referer : "/vocabulary");
    }

    /**
     * 단어장 항목을 삭제합니다.
     */
    @PostMapping("/{id}/delete")
    public String delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        vocabularyService.delete(userDetails.getMemberId(), id);
        redirectAttributes.addFlashAttribute("message", "단어장에서 삭제되었습니다.");
        return "redirect:/vocabulary";
    }
}
