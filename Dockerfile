FROM eclipse-temurin:17

COPY ./target/hau-ecommerce-0.0.1-SNAPSHOT.jar hau-ecommerce.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/hau-ecommerce.jar"]

#ENV DATABASE_HOST=localhost
#ENV DATABASE_PORT=27017
#ENV APP_PORT=8080
#
#WORKDIR /app
#EXPOSE $APP_PORT
#
#CMD ["java", "-jar", "hau-ecommerce.jar", "--db-host", "$DATABASE_HOST", "--db-port", "$DATABASE_PORT"]