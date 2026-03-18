FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /home/app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean compile spring-boot:process-aot package -DskipTests --no-transfer-progress
RUN rm -rf /root/.m2/repository

FROM eclipse-temurin:25-jre-alpine AS final
WORKDIR /home
COPY --from=build /home/app/target/*.jar /home/my-app.jar
RUN java -Djarmode=tools -jar my-app.jar extract
RUN rm my-app.jar
RUN java -XX:AOTCacheOutput=my-app.aot -XX:+AOTClassLinking -Dspring.aot.enabled=true -Dspring.context.exit=onRefresh -Dspring.profiles.active=train -jar /home/my-app/my-app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -XX:AOTCache=my-app.aot -XX:AOTMode=on -XX:+AOTClassLinking -Dspring.aot.enabled=true -Dspring.main.lazy-initialization=true -jar /home/my-app/my-app.jar" ]