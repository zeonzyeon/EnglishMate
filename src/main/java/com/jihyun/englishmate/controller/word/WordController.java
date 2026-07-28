package com.jihyun.englishmate.controller.word;

import com.jihyun.englishmate.dto.word.WordUpdateRequest;
import com.jihyun.englishmate.entity.word.Word;
import com.jihyun.englishmate.service.word.WordService;
import com.jihyun.englishmate.util.word.PartOfSpeechLabels;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * Word 의미와 품사 수정 화면 요청을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    /**
     * Word 의미/품사 수정 화면을 반환합니다.
     */
    @GetMapping("/words/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            @RequestParam(value = "redirectUrl", required = false, defaultValue = "/vocabulary") String redirectUrl,
            Model model
    ) {
        Word word = wordService.findById(id);
        model.addAttribute("word", word);
        model.addAttribute("form", new WordUpdateRequest(word.getMeaning(), word.getPartOfSpeech()));
        model.addAttribute("partsOfSpeech", PartOfSpeechLabels.options());
        model.addAttribute("redirectUrl", redirectUrl);
        return "word/edit";
    }

    /**
     * Word 의미/품사 수정 요청을 처리합니다.
     */
    @PostMapping("/words/{id}/edit")
    public String update(
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

        wordService.update(id, request);
        redirectAttributes.addFlashAttribute("message", "단어 정보가 수정되었습니다.");
        return "redirect:" + redirectUrl;
    }
}
