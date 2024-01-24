FROM openjdk:17-alpine
LABEL authors="ilboogl"
RUN mkdir /app
COPY thinktory.jar /app/thinktory.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "thinktory.jar"]
