FROM openjdk:11-jdk AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
FROM openjdk:11-slim
COPY --from=builder build/libs/*.jar member-service.jar
VOLUME /tmp
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "member-service.jar"]