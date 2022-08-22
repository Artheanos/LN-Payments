FROM openjdk:17
WORKDIR /
EXPOSE 8080
ARG JAR_FILE=target/webservice-*.jar
ADD ${JAR_FILE} webservice.jar
ENTRYPOINT ["java", "-jar", "/webservice.jar"]