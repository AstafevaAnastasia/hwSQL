FROM openjdk:11
COPY app-deadline.jar /app/app-deadline.jar
WORKDIR /app
CMD ["java", "-jar", "app-deadline.jar"]