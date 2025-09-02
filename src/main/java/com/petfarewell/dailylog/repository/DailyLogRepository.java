package com.petfarewell.dailylog.repository;

import com.petfarewell.dailylog.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    List<DailyLog> findByUserIdAndDeletedFalseOrderByLogDateDesc(Long userId);

    boolean existsByUserIdAndLogDateAndDeletedFalse(Long userId, LocalDate logDate); // 하루 한 개 쓰기
}
