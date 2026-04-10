# ── Stage 1: build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copia só o pom primeiro para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copia o código e gera o JAR (sem rodar testes)
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria diretório de uploads
RUN mkdir -p uploads/resumes

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]