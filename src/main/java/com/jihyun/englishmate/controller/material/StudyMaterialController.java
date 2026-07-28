package com.jihyun.englishmate.controller.material;

import com.jihyun.englishmate.dto.material.StudyMaterialCreateRequest;
import com.jihyun.englishmate.dto.material.StudyMaterialResponse;
import com.jihyun.englishmate.dto.material.StudyMaterialUpdateRequest;
import com.jihyun.englishmate.dto.word.ExtractedWordResponse;
import com.jihyun.englishmate.dto.word.WordExtractResult;
import com.jihyun.englishmate.security.member.CustomUserDetails;
import com.jihyun.englishmate.service.material.StudyMaterialService;
import com.jihyun.englishmate.service.word.WordExtractService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 학습 지문 화면 요청을 처리합니다.
 */
@Controller
@RequestMapping("/materials")
@RequiredArgsConstructor
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;
    private final WordExtractService wordExtractService;

    /**
     * 로그인한 회원의 학습 지문 목록을 조회합니다.
     */
    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<StudyMaterialResponse> materials = studyMaterialService.findAllByMember(userDetails.getMemberId());
        model.addAttribute("materials", materials);
        model.addAttribute("nickname", userDetails.getNickname());
        return "materials/list";
    }

    /**
     * 학습 지문 등록 화면을 반환합니다.
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("form", new StudyMaterialCreateRequest("", ""));
        return "materials/form";
    }

    /**
     * 학습 지문 등록 요청을 처리합니다.
     */
    @PostMapping
    public String create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("form") StudyMaterialCreateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            return "materials/form";
        }

        Long materialId = studyMaterialService.create(userDetails.getMemberId(), request);
        redirectAttributes.addFlashAttribute("message", "학습 자료가 등록되었습니다.");
        return "redirect:/materials/" + materialId;
    }

    /**
     * 학습 지문 상세 화면을 반환합니다.
     */
    @GetMapping("/{id}")
    public String detail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            Model model
    ) {
        StudyMaterialResponse material = studyMaterialService.findById(userDetails.getMemberId(), id);
        List<ExtractedWordResponse> extractedWords = wordExtractService.findExtractedWords(userDetails.getMemberId(), id);
        model.addAttribute("material", material);
        model.addAttribute("extractedWords", extractedWords);
        model.addAttribute("extractedWordCount", extractedWords.size());
        return "materials/detail";
    }

    /**
     * 학습 지문 수정 화면을 반환합니다.
     */
    @GetMapping("/{id}/edit")
    public String updateForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            Model model
    ) {
        StudyMaterialResponse material = studyMaterialService.findById(userDetails.getMemberId(), id);
        model.addAttribute("mode", "edit");
        model.addAttribute("materialId", id);
        model.addAttribute("form", new StudyMaterialUpdateRequest(material.title(), material.content()));
        return "materials/form";
    }

    /**
     * 학습 지문 수정 요청을 처리합니다.
     */
    @PostMapping("/{id}")
    public String update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @ModelAttribute("form") StudyMaterialUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("materialId", id);
            return "materials/form";
        }

        studyMaterialService.update(userDetails.getMemberId(), id, request);
        redirectAttributes.addFlashAttribute("message", "학습 자료가 수정되었습니다.");
        return "redirect:/materials/" + id;
    }

    /**
     * 학습 지문 삭제 요청을 처리합니다.
     */
    @PostMapping("/{id}/delete")
    public String delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        studyMaterialService.delete(userDetails.getMemberId(), id);
        redirectAttributes.addFlashAttribute("message", "학습 자료가 삭제되었습니다.");
        return "redirect:/materials";
    }

    /**
     * 학습 지문에서 단어를 추출하고 저장합니다.
     */
    @PostMapping("/{id}/extract")
    public String extractWords(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        WordExtractResult result = wordExtractService.extractAndSave(userDetails.getMemberId(), id);
        redirectAttributes.addFlashAttribute("extractResult", result);
        redirectAttributes.addFlashAttribute("message", "단어 추출이 완료되었습니다.");
        return "redirect:/materials/" + id;
    }
}
