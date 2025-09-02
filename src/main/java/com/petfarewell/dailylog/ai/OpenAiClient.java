package com.petfarewell.dailylog.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    @Value("${AI_API_KEY}")
    private String apikey;

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    public String generateReflection(String userText) {
        String system = """
                당신은 상실을 겪은 사람에게 조용히 공감하는 도우미입니다.
                다음 글을 읽고 한국어로 120자 이내의 따뜻한 '한 줄 공감문'만 반환하세요.
                조언/진단/명령/질문 금지. 오직 한 문장만.
                """;

        String body = """
                {
                  "model": "%s",
                  "temperature": 0.2,
                  "messages": [
                    {"role":"system","content":%s},
                    {"role":"user","content":%s}
                  ]
                }
                """.formatted(MODEL, json(system), json(userText));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apikey);

        ResponseEntity<String> res = restTemplate.exchange(
                URL, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

        try {
            JsonNode root = om.readTree(res.getBody());
            return root.path("choices").get(0).path("message").path("content").asText().trim();
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패", e);
        }
    }

    public String generateTopic() {
        String systemPrompt = """
            너는 반려동물 상실을 겪은 사람들에게 감정 정리를 돕는 글쓰기 가이드 도우미야.
            오늘 하루를 돌아볼 수 있는 일기 주제를 1개 생성해줘.
            조건:
            - 한국어로
            - 20자 이내
            - 질문형 또는 설명형 문장
            - 위로와 성찰을 유도하는 부드러운 문장
            """;

        String userPrompt = "오늘의 일기 주제를 하나 생성해줘.";

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
            return root.path("choices").get(0).path("message").path("content").asText().trim();
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패", e);
        }
    }

    private String json(String s) {
        try { return om.writeValueAsString(s); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
