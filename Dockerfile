# Native Image
#FROM ghcr.io/graalvm/native-image-community:25 AS builder
#WORKDIR /app
#COPY . .
#RUN microdnf install -y maven findutils
#RUN mvn -Pnative native:compile -e -B -DskipTests --no-transfer-progress
#FROM debian:bookworm-slim
#WORKDIR /app
#COPY --from=builder /app/target/allpad-api .
#EXPOSE 8080
#ENTRYPOINT ["./allpad-api"]

# Java Image
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /home/app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -B -DskipTests --no-transfer-progress
RUN rm -rf /root/.m2/repository

FROM eclipse-temurin:25-jre-alpine AS final
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /usr/local/lib/app.jar" ]