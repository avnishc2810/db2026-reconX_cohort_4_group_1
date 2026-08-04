# ============================================================================
# TICKET-ADV146 — Backend multi-stage Dockerfile (Temurin JDK 25 -> JRE 25 alpine)
#
# Stage 1: build the JAR with Temurin JDK 25 + Maven.
# Stage 2: copy ONLY the JAR onto a slim JRE alpine runtime image.
# ============================================================================

FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw -q -B dependency:go-offline || true
COPY src src
RUN ./mvnw -q -B -DskipTests package && \
    mv target/reconx-*.jar app.jar

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S reconx && adduser -S reconx -G reconx
USER reconx
WORKDIR /app
COPY --from=build /workspace/app.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
