package com.petfarewell.dailylog.repository;

import com.petfarewell.dailylog.entity.DailyTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyTopicRepository extends JpaRepository<DailyTopic, Long> {
    Optional<DailyTopic> findByDate(LocalDate date);
}
