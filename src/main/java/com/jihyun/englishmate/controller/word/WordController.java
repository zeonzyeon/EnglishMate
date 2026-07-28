package com.jihyun.englishmate.controller.word;

import com.jihyun.englishmate.dto.word.WordUpdateRequest;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.security.member.CustomUserDetails;
import com.jihyun.englishmate.service.word.WordService;
import com.jihyun.englishmate.util.word.PartOfSpeechLabels;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 회원별 단어 의미와 품사 수정 화면 요청을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    /**
     * 공통 Word 정보와 회원 개인 단어장 값을 함께 보여줍니다.
     */
    @GetMapping("/words/{id}/edit")
    public String editForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(value = "redirectUrl", required = false, defaultValue = "/vocabulary") String redirectUrl,
            Model model
    ) {
        Word word = wordService.findById(id);
        model.addAttribute("word", word);
        model.addAttribute("form", wordService.findUpdateRequest(userDetails.getMemberId(), id));
        model.addAttribute("partsOfSpeech", PartOfSpeechLabels.options());
        model.addAttribute("redirectUrl", redirectUrl);
        return "word/edit";
    }

    /**
     * 회원 개인 단어장에 의미와 품사를 저장합니다.
     */
    @PostMapping("/words/{id}/edit")
    public String update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(value = "redirectUrl", required = false, defaultValue = "/vocabulary") String redirectUrl,
            @Valid @ModelAttribute("form") WordUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Word word = wordService.findById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("word", word);
            model.addAttribute("partsOfSpeech", PartOfSpeechLabels.options());
            model.addAttribute("redirectUrl", redirectUrl);
            return "word/edit";
        }

        wordService.update(userDetails.getMemberId(), id, request);
        redirectAttributes.addFlashAttribute("message", "단어 정보가 수정되었습니다.");
        return "redirect:" + redirectUrl;
    }
}
