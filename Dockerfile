# Java 17 기반 이미지 사용
FROM openjdk:17

# JAR 파일 복사 (빌드시 이름 고정 또는 빌드 후 app.jar로 rename)
COPY app.jar app.jar

# 포트 오픈
EXPOSE 3004

# 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]