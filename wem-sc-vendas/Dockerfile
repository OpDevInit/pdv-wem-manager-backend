# Estágio 1: Compilar a aplicação usando Maven
FROM maven:3.8.8-jakartaee-10 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Rodar a aplicação usando o Java
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
