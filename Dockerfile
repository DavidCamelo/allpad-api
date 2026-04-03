FROM eclipse-temurin:25-jdk-alpine
WORKDIR /home/app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean compile spring-boot:process-aot package -DskipTests --no-transfer-progress
RUN rm -rf /root/.m2/repository
RUN cp target/*.jar /home/my-app.jar
WORKDIR /home
RUN rm -rf app
RUN java -Djarmode=tools -jar my-app.jar extract
RUN java -XX:AOTCacheOutput=my-app.aot -Dspring.context.exit=onRefresh -Dspring.profiles.active=train -jar /home/my-app/my-app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /home/my-app/my-app.jar" ]