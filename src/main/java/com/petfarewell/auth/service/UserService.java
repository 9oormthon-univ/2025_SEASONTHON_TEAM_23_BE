package com.petfarewell.auth.service;

import java.util.Optional;

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

    @Transactional
    public User registerOrUpdateKakaoUser(KakaoUserInfoResponse userInfo) {
        Optional<User> existingUserOpt = userRepository.findByKakaoId(userInfo.getId());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            boolean updated = false;

            if (userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getProfile() != null) {
                String newNickname = userInfo.getKakaoAccount().getProfile().getNickName();
                String newProfileImage = userInfo.getKakaoAccount().getProfile().getProfileImageUrl();

                if (newNickname != null && !newNickname.equals(existingUser.getNickname())) {
                    existingUser.setNickname(newNickname);
                    updated = true;
                }

                if (newProfileImage != null && !newProfileImage.equals(existingUser.getProfileImageUrl())) {
                    existingUser.setProfileImageUrl(newProfileImage);
                    updated = true;
                }
            }

            if (updated) {
                log.info("Updated user profile for kakaoId: {}", existingUser.getKakaoId());
            }

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
            return userRepository.save(newUser);
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

