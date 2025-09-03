package com.petfarewell.dailylog.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.dailylog.dto.*;
import com.petfarewell.dailylog.service.DailyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "데일리로그 API", description = "사용자가 랜덤 주제를 받고, 데일리 로그를 작성하고 확인하는 API입니다.")
@RestController
@RequestMapping("/daily-log")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService service;

    @Operation(summary = "랜덤 주제 생성 API", description = "AI가 매일 1개의 주제를 던져줍니다.")
    @GetMapping("/topic")
    public ResponseEntity<DailyTopicResponse> getRandomTopic() {
        return ResponseEntity.ok(service.getRandomTopic());
    }

    @Operation(summary = "일기 작성 및 공감문 생성 API", description = "사용자가 일기를 작성하고 이를 저장하고, 바탕으로 하여 AI가 짧은 공감문을 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<DailyLogResponse> create(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody DailyLogRequest request) {
        DailyLogResponse response = service.create(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "일기 전체 리스트 조회 API", description = "사용자가 작성한 일기를 전체 조회합니다.")
    @GetMapping("/list")
    public List<DailyLogSummaryResponse> getAllLogs(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.getAllLogs(userDetails.getId());
    }

    @Operation(summary = "일기 상세 조회 API", description = "해당 일기에 대한 정보를 상세 조회합니다.")
    @GetMapping("/{logId}")
    public ResponseEntity<DailyLogDetailResponse> getDailyLogDetail(
            @PathVariable("logId") Long logId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DailyLogDetailResponse response = service.getDailyLogDetail(userDetails.getId(), logId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일기 수정 API", description = "사용자가 수정한 일기를 저장합니다. 여기서도 ai 공감문 버튼을 활성화한 경우 공감문을 수정된 일기 기반으로 재생성하여 저장합니다.")
    @PutMapping("/update/{logId}")
    public void updateDailyLog(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("logId") Long logId, @RequestBody DailyLogUpdateRequest request) {
        service.update(userDetails.getId(), logId, request);
    }

    @Operation(summary = "일기 삭제 API", description = "일기를 삭제 처리합니다.")
    @DeleteMapping("/delete/{logId}")
    public void deleteDailyLog(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("logId") Long logId) {
        service.delete(userDetails.getId(), logId);
    }
}
