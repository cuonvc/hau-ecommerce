FROM eclipse-temurin:17

COPY ./target/order-service-core-0.0.1-SNAPSHOT.jar order-service-core.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/order-service-core.jar"]