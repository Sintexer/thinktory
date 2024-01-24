FROM openjdk:17-alpine
LABEL authors="ilboogl"

ENTRYPOINT ["java", "-jar", "/opt/mibe/thinktory/thinktory.jar"]
