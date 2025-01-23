FROM amazoncorretto:17
ENV DB_HOST microservice-db
ENV MQ_HOST activemq
WORKDIR /app
COPY target/microservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
