FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /home/app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests --no-transfer-progress
RUN rm -rf /root/.m2/repository

FROM eclipse-temurin:25-jre-alpine AS final
WORKDIR /home
COPY --from=build /home/app/target/*.jar /home/my-app.jar
RUN java -Djarmode=tools -jar my-app.jar extract
RUN rm my-app.jar
RUN java -XX:AOTMode=record -XX:AOTConfiguration=my-spring-cache.aotconf -Dspring.profiles.active=train -Dspring.context.exit=onRefresh -jar /home/my-app/my-app.jar
RUN java -XX:AOTMode=create -XX:AOTConfiguration=my-spring-cache.aotconf -XX:AOTCache=my-spring-cache.aot -Dspring.profiles.active=train -Dspring.context.exit=onRefresh -jar /home/my-app/my-app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -XX:AOTCache=my-spring-cache.aot -jar /home/my-app/my-app.jar" ]