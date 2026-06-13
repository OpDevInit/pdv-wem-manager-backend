# Estágio 1: Compilar a aplicação usando Maven oficial com Java 17
FROM maven:3.8.7-openjdk-18 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Rodar a aplicação usando o Java 17 estável
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
