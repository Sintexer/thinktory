FROM openjdk:17-alpine
LABEL authors="ilboogl"

ENTRYPOINT ["java", "-jar", "app/thinktory.jar"]