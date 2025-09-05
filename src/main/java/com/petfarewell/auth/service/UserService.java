package com.petfarewell.auth.service;

import java.util.Optional;

import com.petfarewell.letter.entity.Notification;
import com.petfarewell.letter.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petfarewell.auth.dto.response.KakaoUserInfoResponse;
import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final NotificationRepository tributeNotificationRepositoy;

    @Transactional
    public User registerOrUpdateKakaoUser(KakaoUserInfoResponse userInfo) {
        Optional<User> existingUserOpt = userRepository.findByKakaoId(userInfo.getId());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            return existingUser;
        } else {
            User newUser = User.createFromKakao(
                    userInfo.getId(),
                    userInfo.getKakaoAccount() != null ? userInfo.getKakaoAccount().getEmail() : null,
                    userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getProfile() != null
                            ? userInfo.getKakaoAccount().getProfile().getNickName() : null,
                    userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getProfile() != null
                            ? userInfo.getKakaoAccount().getProfile().getProfileImageUrl() : null
            );
            log.info("Created new user with kakaoId: {}", newUser.getKakaoId());

            User user = userRepository.save(newUser);
            tributeNotificationRepositoy.save(new Notification(newUser));

            return user;
        }
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void updateRefreshToken(User user, String refreshToken) {
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public boolean validateRefreshToken(User user, String refreshToken) {
        return refreshToken.equals(user.getRefreshToken());
    }

    @Transactional
    public void logout(User user) {
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}

