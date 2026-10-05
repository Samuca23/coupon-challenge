# Stage 1: build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# Stage 2: runtime
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# curl é necessário pro HEALTHCHECK; a imagem jre não vem com ele por padrão
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN useradd --create-home --shell /usr/sbin/nologin coupon
USER coupon

COPY --from=build /app/target/coupon-challenge-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=5 \
    CMD curl -f http://localhost:8080/v3/api-docs || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
