FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /ProductMicroservice/pom.xml
WORKDIR /ProductMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /ProductMicroservice/src
WORKDIR /ProductMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /ProductMicroservice
COPY --from=build /ProductMicroservice/target/ProductMicroservice-0.0.1-SNAPSHOT.jar /ProductMicroservice/ProductMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "ProductMicroservice.jar"]