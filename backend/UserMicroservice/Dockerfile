FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /UserMicroservice/pom.xml
WORKDIR /UserMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /UserMicroservice/src
WORKDIR /UserMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /UserMicroservice
COPY --from=build /UserMicroservice/target/UserMicroservice-0.0.1-SNAPSHOT.jar /UserMicroservice/UserMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "UserMicroservice.jar"]