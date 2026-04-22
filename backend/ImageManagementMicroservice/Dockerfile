FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /ImageManagementMicroservice/pom.xml
WORKDIR /ImageManagementMicroservice

RUN mvn dependency:go-offline

COPY ./src/ /ImageManagementMicroservice/src
WORKDIR /ImageManagementMicroservice

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /ImageManagementMicroservice
COPY --from=build /ImageManagementMicroservice/target/ImageManagementMicroservice-0.0.1-SNAPSHOT.jar /ImageManagementMicroservice/ImageManagementMicroservice.jar
EXPOSE 8080
CMD ["java", "-jar", "ImageManagementMicroservice.jar"]
