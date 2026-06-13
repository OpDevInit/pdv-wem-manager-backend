# Estágio 1: Compilar a aplicação usando Maven com JDK 17 da Temurin
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Rodar a aplicação usando apenas o JRE 17 (mais leve e seguro)
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
