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
RUN java -XX:AOTMode=record -XX:AOTConfiguration=my-app.aotconf -Dspring.context.exit=onRefresh -Dspring.profiles.active=train -jar my-app/my-app.jar
RUN java -XX:AOTMode=create -XX:AOTConfiguration=my-app.aotconf -XX:AOTCache=my-app.aot -Dspring.context.exit=onRefresh -Dspring.profiles.active=train -jar my-app/my-app.jar
RUN rm my-app.aotconf
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -XX:AOTCache=my-app.aot -Dspring.profiles.active=prod -jar my-app/my-app.jar" ]