FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /RecommendationMicroservice/pom.xml
WORKDIR /RecommendationMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /RecommendationMicroservice/src
WORKDIR /RecommendationMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /RecommendationMicroservice
COPY --from=build /RecommendationMicroservice/target/RecommendationMicroservice-0.0.1-SNAPSHOT.jar /RecommendationMicroservice/RecommendationMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "RecommendationMicroservice.jar"]
