package com.petfarewell.letter.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.letter.dto.request.LetterRequest;
import com.petfarewell.letter.dto.response.LetterResponse;
import com.petfarewell.letter.dto.response.TributeResponse;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.LetterTribute;
import com.petfarewell.letter.service.LetterService;
import com.petfarewell.letter.service.TributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letters")
@Tag(name = "Letter", description = "한마디 편지")
public class LetterController {
    private final LetterService letterService;
    private final TributeService tributeService;

    @PostMapping
    @Operation(summary = "한마디 편지 작성", description = "한마디 편지를 DB에 저장")
    public ResponseEntity<LetterResponse> writeLetter(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute LetterRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Letter savedLetter = letterService.saveLetter(userDetails.getId(), request, imageFile);

        LetterResponse response = LetterResponse.from(savedLetter);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/public")
    @Operation(summary = "전체 공개 편지 리스트 조회", description = "전체 공개로 설정된 모든 사용자의 편지 리스트 조회")
    public ResponseEntity<List<LetterResponse>> getPublicLetters() {
        List<Letter> letters = letterService.findPublicLetters();

        List<LetterResponse> response = letters.stream()
                .map(LetterResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 편지 리스트 조회", description = "내가 작성한 모든 편지 리스트 조회")
    public ResponseEntity<List<LetterResponse>> getMyLetters(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Letter> letters = letterService.findMyLetters(userDetails.getId());

        List<LetterResponse> response = letters.stream()
                .map(LetterResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{letterId}")
    @Operation(summary = "단일 편지 상세 보기", description = "특정 id를 가진 단일 편지를 조회")
    public ResponseEntity<LetterResponse> getLetter(@PathVariable("letterId") Long letterId) {
        Letter findLetter = letterService.findLetterById(letterId);

        LetterResponse response = LetterResponse.from(findLetter);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{letterId}")
    @Operation(summary = "편지 수정", description = "특정 id를 가진 편지 수정")
    public ResponseEntity<LetterResponse> updateLetter(
            @PathVariable("letterId") Long letterId,
            @ModelAttribute LetterRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Letter updatedLetter = letterService.updateLetter(letterId, userDetails.getId(), request, imageFile);

        LetterResponse response = LetterResponse.from(updatedLetter);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{letterId}")
    @Operation(summary = "편지 삭제", description = "특정 id를 가진 편지 삭제")
    public ResponseEntity<Void> deleteLetter(
            @PathVariable("letterId") Long letterId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        letterService.deleteLetter(letterId, userDetails.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{letterId}/tributes")
    @Operation(summary = "특정 편지의 헌화 리스트 조회", description = "letterId에 해당하는 편지에 달린 모든 헌화 리스트 조회")
    public ResponseEntity<List<TributeResponse>> getTributesForLetter(@PathVariable Long letterId) {

        List<LetterTribute> tributes = tributeService.findTributesByLetterId(letterId);

        List<TributeResponse> response = tributes.stream()
                .map(TributeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{letterId}/tributes")
    @Operation(summary = "디지털 헌화", description = "특정 편지에 헌화")
    public ResponseEntity<TributeResponse> addTribute(
            @PathVariable Long letterId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LetterTribute savedTribute = tributeService.addTribute(letterId, userDetails.getId());

        TributeResponse response = TributeResponse.from(savedTribute);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
