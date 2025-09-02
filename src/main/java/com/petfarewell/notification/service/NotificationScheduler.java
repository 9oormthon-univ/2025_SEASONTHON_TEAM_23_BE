package com.petfarewell.notification.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.LetterTribute;
import com.petfarewell.letter.repository.LetterTributeRepository;
import com.petfarewell.notification.entity.Notification;
import com.petfarewell.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final LetterTributeRepository letterTributeRepository;
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 */3 * * *")
    @Transactional
    public void createTributeNotifications() {
        log.info("Starting notification service");

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(3, ChronoUnit.HOURS);

        List<LetterTribute> recentTributes = letterTributeRepository.findAllByCreatedAtBetween(startTime, endTime);

        Map<User, Long> tributeCountsByRecipient = recentTributes.stream()
                .collect(Collectors.groupingBy(
                        tribute -> tribute.getLetter().getUser(),
                        Collectors.counting()
                ));

        List<Notification> newNotifications = tributeCountsByRecipient.entrySet().stream()
                .map(entry -> {
                    User recipient = entry.getKey();
                    Long count = entry.getValue();
                    String content = count + "명의 사람들이 헌화를 보냈어요.";

                    return Notification.builder()
                            .recipient(recipient)
                            .content(content)
                            .build();
                })
                .collect(Collectors.toList());

        if (!newNotifications.isEmpty()) {
            notificationRepository.saveAll(newNotifications);
        }
    }
}

