package com.petfarewell.dailylog.service;


import com.petfarewell.dailylog.ai.OpenAiClient;
import com.petfarewell.dailylog.dto.*;
import com.petfarewell.dailylog.entity.DailyLog;
import com.petfarewell.dailylog.entity.DailyTopic;
import com.petfarewell.dailylog.repository.DailyLogRepository;
import com.petfarewell.dailylog.repository.DailyTopicRepository;
import com.petfarewell.global.exception.AlreadyWrittenException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final DailyTopicRepository dailyTopicRepository;
    private final OpenAiClient openAiClient;

    @Transactional
    public DailyLogResponse create(Long userId, DailyLogRequest request) {
        LocalDate date = request.getLogDate() != null ? request.getLogDate() : LocalDate.now();

//        if (dailyLogRepository.existsByUserIdAndLogDateAndDeletedFalse(userId, date)) {
//            throw new AlreadyWrittenException("해당 날짜에 이미 일기를 작성하셨습니다.");
//        }

        // 오늘 날짜의 주제 가져오기 (없으면 예외 또는 생성)
        String topic = getRandomTopic().getTopic();

        DailyLog log = new DailyLog();
        log.setUserId(userId);
        log.setLogDate(date);
        log.setMood(request.getMood());
        log.setContent(request.getContent());
        log.setTopic(topic);
        DailyLog savedLog = dailyLogRepository.save(log);

        if (request.isNeedAiReflection() && log.getContent() != null && !log.getContent().isBlank()) {
            // 비동기로 공감문 생성 후 저장
            generateAndSaveReflectionAsync(log.getId(), log.getContent());
        }
        return DailyLogResponse.builder()
                .id(savedLog.getId())
                .build();
    }

    @Async
    @Transactional
    public void generateAndSaveReflectionAsync(Long logId, String content) {
        String reflection = openAiClient.generateReflection(content);
        dailyLogRepository.findById(logId).ifPresent(l -> {
            l.setAiReflection(reflection);
        });
    }

    public DailyTopicResponse getRandomTopic() {
        LocalDate today = LocalDate.now();

        return dailyTopicRepository.findByDate(today)
                .map(d -> DailyTopicResponse.builder()
                        .topic(d.getTopic())
                        .date(d.getDate())
                        .build())
                .orElseGet(() -> {
                    String topic = openAiClient.generateTopic();
                    DailyTopic saved = dailyTopicRepository.save(
                            DailyTopic.builder().topic(topic).date(today).build()
                    );
                    return DailyTopicResponse.builder()
                            .topic(saved.getTopic())
                            .date(saved.getDate())
                            .build();
                });
    }

    public List<DailyLogSummaryResponse> getAllLogs(Long userId) {
        List<DailyLog> logs = dailyLogRepository.findByUserIdAndDeletedFalseOrderByLogDateDesc(userId);

        return logs.stream()
                .map(log -> DailyLogSummaryResponse.builder()
                        .id(log.getId())
                        .logDate(log.getLogDate())
                        .topic(log.getTopic())
                        .preview(buildPreview(log.getContent()))
                        .mood(log.getMood())
                        .build())
                .toList();
    }

    private String buildPreview(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() <= 10 ? content : content.substring(0, 10) + " 더보기";
    }

    @Transactional(readOnly = true)
    public DailyLogDetailResponse getDailyLogDetail(Long userId, Long logId) {
        DailyLog log = dailyLogRepository.findByIdAndUserIdAndDeletedFalse(logId, userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 일기를 찾을 수 없습니다."));

        return DailyLogDetailResponse.builder()
                .id(log.getId())
                .logDate(log.getLogDate())
                .topic(log.getTopic())
                .content(log.getContent())
                .mood(log.getMood())
                .aiReflection(log.getAiReflection())
                .build();
    }

}
