package com.petfarewell.dailylog.repository;

import com.petfarewell.dailylog.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    List<DailyLog> findAllByUserIdAndLogDateBetweenAndDeletedFalseOrderByLogDateDesc(
            Long userId, LocalDate start, LocalDate end
    );
}
