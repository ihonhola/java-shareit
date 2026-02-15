FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
#COPY src/main/resources/schema.sql /schema.sql
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]