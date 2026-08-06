package com.jihyun.englishmate.controller.material;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Guest 정책에서 안내된 /study-materials 경로를 기존 /materials 화면으로 연결합니다.
 */
@Controller
public class StudyMaterialAliasController {

    @GetMapping("/study-materials")
    public String redirectToMaterials() {
        return "redirect:/materials";
    }

    @GetMapping("/study-materials/{id}")
    public String redirectToMaterialDetail(@PathVariable Long id) {
        return "redirect:/materials/" + id;
    }
}
