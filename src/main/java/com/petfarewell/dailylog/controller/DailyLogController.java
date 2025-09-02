package com.petfarewell.dailylog.controller;

import com.petfarewell.dailylog.dto.*;
import com.petfarewell.dailylog.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daily-log")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService service;


    @PostMapping("/create")
    public ResponseEntity<DailyLogResponse> create(@RequestParam("userId") Long userId, @RequestBody DailyLogRequest request) {
        DailyLogResponse response = service.create(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/topic")
    public ResponseEntity<DailyTopicResponse> getRandomTopic() {
        return ResponseEntity.ok(service.getRandomTopic());
    }

    @GetMapping("/list")
    public List<DailyLogSummaryResponse> getAllLogs(@RequestParam("userId") Long userId) {
        return service.getAllLogs(userId);
    }

    @GetMapping("/detail/{id}")
    public DailyLogDetailResponse getLogDetail(@RequestParam("userId") Long userId, @RequestParam("logId") Long logId) {
        return service.getDetail(userId, logId);
    }
}
