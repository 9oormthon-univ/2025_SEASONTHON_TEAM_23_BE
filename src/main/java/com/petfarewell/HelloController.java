package com.petfarewell;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "테스트 API", description = "테스트용 API입니다.")
@RestController
@RequestMapping("/test")
public class HelloController {

    @Operation(summary = "테스트", description = "테스트 문구 제공.")
    @GetMapping
    public String hello() {
        return "deploy test.";
    }
}