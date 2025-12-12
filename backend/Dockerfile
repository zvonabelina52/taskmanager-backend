# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/quarkus-app/lib/ /app/lib/
COPY --from=build /app/target/quarkus-app/*.jar /app/
COPY --from=build /app/target/quarkus-app/app/ /app/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/q/health || exit 1

CMD ["java", "-jar", "/app/quarkus-run.jar"]