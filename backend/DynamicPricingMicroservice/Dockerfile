FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /DynamicPricingMicroservice/pom.xml
WORKDIR /DynamicPricingMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /DynamicPricingMicroservice/src
WORKDIR /DynamicPricingMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /DynamicPricingMicroservice
COPY --from=build /DynamicPricingMicroservice/target/DynamicPricingMicroservice-0.0.1-SNAPSHOT.jar /DynamicPricingMicroservice/DynamicPricingMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "DynamicPricingMicroservice.jar"]
