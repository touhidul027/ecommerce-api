FROM openjdk:21-jdk-slim
RUN chmod 1777 /tmp
VOLUME /tmp
COPY target/ecommerce-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]