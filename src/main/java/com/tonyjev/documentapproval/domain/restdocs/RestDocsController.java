package com.tonyjev.documentapproval.domain.restdocs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping
public class RestDocsController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
