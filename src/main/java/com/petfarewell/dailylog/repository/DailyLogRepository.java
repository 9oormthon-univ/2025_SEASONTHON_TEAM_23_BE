package com.petfarewell.dailylog.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.dailylog.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    long countByUserAndDeletedFalse(User user);
    long countByUserAndDeletedFalseAndLogDateBetween(User user, LocalDate startDate, LocalDate endDate);

    Optional<DailyLog> findByIdAndUserAndDeletedFalse(Long id, User user);

    List<DailyLog> findByUserAndDeletedFalseOrderByLogDateDesc(User user);

    boolean existsByUserAndLogDateAndDeletedFalse(User user, LocalDate logDate); // 하루 한 개 쓰기

    long countByUserAndDeletedFalseAndMoodAndLogDateBetween(
            User user, Integer mood, LocalDate startDate, LocalDate endDate
    );
}
