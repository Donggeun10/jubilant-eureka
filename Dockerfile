## build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build
COPY pom.xml .
# 1) dependency caching
RUN mvn -B dependency:resolve

# 2) source 복사 & package
COPY src/ /build/src/
RUN mvn package -DskipTests &&  \
    java -Djarmode=tools -jar target/chatbackend.jar extract --destination application

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /home/appuser

# 2) Jar 복사
COPY --chown=appuser --from=builder /build/application/lib ./lib
COPY --chown=appuser --from=builder /build/application/chatbackend.jar ./chatbackend.jar

# 3) 실행
ENTRYPOINT ["java", "-jar", "-Dhazelcast.shutdownhook.enabled=false","chatbackend.jar"]