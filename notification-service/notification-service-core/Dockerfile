FROM eclipse-temurin:17

COPY ./target/notification-service-core-0.0.1-SNAPSHOT.jar notification-service.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/notification-service.jar"]