FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /AuthMicroservice/pom.xml
WORKDIR /AuthMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /AuthMicroservice/src
WORKDIR /AuthMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /AuthMicroservice
COPY --from=build /AuthMicroservice/target/AuthMicroservice-0.0.1-SNAPSHOT.jar /AuthMicroservice/AuthMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "AuthMicroservice.jar"]
