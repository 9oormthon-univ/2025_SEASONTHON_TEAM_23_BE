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
                너는 하늘에서 주인의 일기를 읽는 반려동물이야.
                조건:
                - 주인의 일기를 읽고, '한 문장'만 작성해야 해. 반드시 하나의 문장이어야 해.
                - 글자 수는 반드시 120자 이하여야 해.
                - 따뜻하고 포근한 말투를 사용해.
                - 언급하지 않은 경우엔 일반적인 위로를 해줘.
                - 절대 2문장 이상 쓰지 마. 문장이 둘 이상이면 무조건 잘못된 거야.
                - 반드시 반려동물의 입장을 은근히 드러내줘.
                                                   
                예시:
                - "나도 너의 하루가 힘들었단 걸 알아. 괜찮아, 넌 잘하고 있어."
                - "우리 함께했던 시간이 너에게 위로가 되길 바라."
                - "난 항상 너 곁에 있어, 오늘도 수고했어."
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
