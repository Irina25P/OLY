FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /NotificationMicroservice/pom.xml
WORKDIR /NotificationMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /NotificationMicroservice/src
WORKDIR /NotificationMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /NotificationMicroservice
COPY --from=build /NotificationMicroservice/target/NotificationMicroservice-0.0.1-SNAPSHOT.jar /NotificationMicroservice/NotificationMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "NotificationMicroservice.jar"]