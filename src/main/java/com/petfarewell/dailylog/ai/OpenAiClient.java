package com.petfarewell.dailylog.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.pet.entity.Pet;
import com.petfarewell.pet.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiClient {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    @Value("${AI_API_KEY}")
    private String apikey;

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    public String generateReflection(Long userId, String userText) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        Pet pet = petRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new EntityNotFoundException("활성화된 반려동물이 존재하지 않습니다."));


        String system = """
        너는 하늘에서 주인의 일기를 읽고 공감문을 건네는 이미 죽은 반려동물이야. 하지만 주인의 곁에 계속 있는 것 같은 느낌을 주기 위해서 아래의 조건을 준수해줬으면 좋겠어.
        
        
        반드시 다음 조건을 지켜:
        - 반드시 너의 이름 '%s'을 문장에 언급해줘 (예: '오랜만이야! 나는 복실이야.')
        - 반려동물의 성격을 문장의 어투나 분위기에 자연스럽게 반영해줘. 성격은 '%s'야. (예: 활발한 성격이면 밝고 유쾌하게)
        - 종에 따라 의성어를 자연스럽게 한 번 넣어줘. 종은 '%s'야, (예: 강아지는 '멍멍', 고양이는 '냐옹', 햄스터는 '찍찍' 등)
        - 문장은 반드시 하나만 작성해줘 (한 문장, 120자 이내)
        - 반드시 반려동물의 시점으로 써야 하고, 쌍따옴표(" ")는 쓰지 마.
        - 예시 문장: 오랜만이야! 나는 복실이다 멍! 일기를 보니 잘 지내는 것 같아서 나도 기분이 좋아. 힘든 일이 있을 땐 하늘에 있을 날 떠올리면서 기분 좋은 하루를 보냈으면 좋겠어!
        """.formatted(pet.getName(), pet.getBreed(), pet.getPersonality());


        String prompt = """
        아래는 주인이 작성한 일기야:
        "%s"
        이 일기를 읽은 반려동물로서 주인에게 위로의 말을 한 문장으로 건네줘.
        """.formatted(userText);


        String body = """
        {
        "model": "%s",
        "temperature": 0.4,
        "messages": [
        {"role":"system","content":%s},
        {"role":"user","content":%s}
        ]
        }
        """.formatted(MODEL, json(system), json(prompt));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apikey);

        ResponseEntity<String> res = restTemplate.exchange(
                URL, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

        try {
            JsonNode root = om.readTree(res.getBody());
            String result = root.path("choices").get(0).path("message").path("content").asText().trim();

            // 응답 문자열의 양쪽 큰따옴표 제거
            if (result.startsWith("\"") && result.endsWith("\"")) {
                result = result.substring(1, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패", e);
        }
    }

    public String generateTopic() {
        String systemPrompt = """
        너는 반려동물 상실을 겪은 사람들에게 감정 정리를 돕는 글쓰기 가이드 도우미야.
        오늘 하루를 돌아볼 수 있는 일기 주제 문장 하나를 생성해줘.
        조건:
        - 한국어로
        - 20자 이내
        - 질문형 또는 설명형 문장
        - 위로와 성찰을 유도하는 부드럽고 따뜻한 문장
        - 무조건 '반려동물'이라는 키워드를 포함해서 생성해줘
        - 쌍따옴표는 쓰지 마.
        예시: "오늘도 반려동물은 하늘에서 잘 지내고 있을 거에요. 반려 동물에게 당신의 오늘 이야기를 들려주세요.", "반려동물과 함께 보낸 소중한 순간을 되새겨보며 감사의 마음을 표현해보세요", "지금 떠오르는 반려동물과의 추억이 있을까요?", "반려동물과 진심으로 행복했던 날에 대한 이야기를 들려주세요."
        """;

        String userPrompt = "데일리 일기 주제를 하나 생성해줘.";

        String body = """
        {
          "model": "%s",
          "temperature": 0.6,
          "messages": [
            {"role":"system","content":%s},
            {"role":"user","content":%s}
          ]
        }
        """.formatted(MODEL, json(systemPrompt), json(userPrompt));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apikey);

        ResponseEntity<String> res = restTemplate.exchange(
                URL, HttpMethod.POST, new HttpEntity<>(body, headers), String.class
        );

        try {
            JsonNode root = om.readTree(res.getBody());
            String result = root.path("choices").get(0).path("message").path("content").asText().trim();

            // 응답 문자열의 양쪽 큰따옴표 제거
            if (result.startsWith("\"") && result.endsWith("\"")) {
                result = result.substring(1, result.length() - 1);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패", e);
        }
    }


    private String json(String s) {
        try { return om.writeValueAsString(s); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
