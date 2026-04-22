FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /OrderMicroservice/pom.xml
WORKDIR /OrderMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /OrderMicroservice/src
WORKDIR /OrderMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /OrderMicroservice
COPY --from=build /OrderMicroservice/target/OrderMicroservice-0.0.1-SNAPSHOT.jar /OrderMicroservice/OrderMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "OrderMicroservice.jar"]